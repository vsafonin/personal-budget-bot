package ru.vladimir.personalAccounterBot.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;
import ru.vladimir.personalAccounterBot.util.ParseLineUtil;

@Getter
@Setter
public class PurchaseTransaction {

	private Shop shop;
	
	private List<ProductData> productDatas;
	
	private BigDecimal sumTransaction;
	
	private long fiscalSign;
	    
    private boolean fromJson;
	
	public PurchaseTransaction(String line,@Nullable Locale locale) throws ParseMessageExp {
		parseFromString(line, locale);
	}
	
	public PurchaseTransaction(JSONObject jsonObject) throws JSONException, ParseMessageExp {
		this.fromJson = true;
		this.fiscalSign = jsonObject.getLong("fiscalSign");
		
		String shopName = jsonObject.getString("user");
		this.shop = new Shop(shopName);
		
		BigDecimal totalSum = jsonObject.getBigDecimal("totalSum");
		this.sumTransaction = totalSum.divide(BigDecimal.valueOf(100));
		
		JSONArray jsonArray = jsonObject.getJSONArray("items");
		for (int i=0; i < jsonArray.length(); i++) {
			ProductData theProductData = new ProductData();
			StringBuilder productNameBuilder = new StringBuilder();
			List<String> lineWithProductName = ParseLineUtil.parse(jsonArray.getJSONObject(i).getString("name"));
			int currentPostion = 0;
			boolean skipPositions = false;
			int howManySkipPosition = 0;
			for (String position: lineWithProductName) {
				if (position.contains(":") && currentPostion == 0) {
					skipPositions = true;
					howManySkipPosition = 2; //this orders from PEREKRESTOK,
				}
				if (skipPositions && currentPostion < howManySkipPosition) {
					currentPostion++;
					continue;
				}
				productNameBuilder.append(position).append(" ");
				currentPostion++;
			}
			Product theProduct = new Product();
			theProduct.setName(productNameBuilder.toString().trim());
			theProductData.setProduct(theProduct);
			
			BigDecimal productPrice = jsonArray.getJSONObject(i).getBigDecimal("price").divide(BigDecimal.valueOf(100));
			BigDecimal quantity = jsonArray.getJSONObject(i).getBigDecimal("quantity");
			
			theProductData.setCost(productPrice);
			theProductData.setQuantity(quantity);
			if (this.productDatas == null) {
				productDatas = new ArrayList<ProductData>();
			}
			this.productDatas.add(theProductData);
		}
	}

	private void parseFromString(String lineOfString, Locale locale ) throws ParseMessageExp {
	
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",locale);
		
		List<String> wordList = new ArrayList<String>();
		try {
			wordList	= ParseLineUtil.parse(lineOfString);
		}
		catch (ParseMessageExp exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.purchaseTransaction.parseError"));
		}
		
		if (wordList.isEmpty()) {
			throw new ParseMessageExp(resourceBundle.getString("exception.purchaseTransaction.parseError"));
		}
		//check length
		if (wordList.size() < 3) {
			throw new ParseMessageExp(resourceBundle.getString("exception.purchaseTransaction.parseError"));
		}
		shop = new Shop(wordList.get(0));
		
		Product theProduct = new Product();
		theProduct.setName(wordList.get(1));
		
		ProductData theProductData = new ProductData();
		theProductData.setProduct(theProduct);
		
		try {
			theProductData.setCost(new BigDecimal(wordList.get(2))); 
		}
		catch (NumberFormatException exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.purchaseTransaction.parseError.sumIsIncorrect"));
		}
		
		
		if (wordList.size() > 3 ) {
			try {
				BigDecimal quantity = new BigDecimal(wordList.get(2));
				theProductData.setQuantity(quantity);
			}
			catch (NumberFormatException e) {
				//i think this is category
				StringBuilder sb = new StringBuilder();
				wordList.stream().skip(2).forEach(w -> sb.append(w).append(" "));
				theProduct.setCategory(new Category(sb.toString().trim()));
			}
		}
		else {
			theProductData.setQuantity(BigDecimal.ONE);
		}
		if (wordList.size() > 4 && theProductData.getQuantity().compareTo(BigDecimal.ZERO) == 1) {
			//i think this is category
			StringBuilder sb = new StringBuilder();
			wordList.stream().skip(3).forEach(w -> sb.append(w).append(" "));
			theProduct.setCategory(new Category(sb.toString().trim()));
		}
		
		productDatas = List.of(theProductData);
	
		this.sumTransaction = theProductData.getCost().multiply(theProductData.getQuantity());
		
	}
}
