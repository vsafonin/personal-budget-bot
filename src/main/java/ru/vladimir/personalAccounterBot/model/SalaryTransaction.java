package ru.vladimir.personalAccounterBot.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;
import ru.vladimir.personalAccounterBot.util.ParseLineUtil;

@Getter
@Setter
public class SalaryTransaction {
	
	private Partner partner;
	
	private BigDecimal sumTransaction;
	
	private String description;
	
	public SalaryTransaction(String line,@Nullable Locale locale) throws ParseMessageExp {
		parseFromString(line, locale);
	}
	
	private void parseFromString(String lineOfString, Locale locale ) throws ParseMessageExp {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",locale);
		List<String> wordList = new ArrayList<String>();
		try {
			wordList	= ParseLineUtil.parse(lineOfString);
		}
		catch (ParseMessageExp exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.salaryTransaction.parseError"));
		}
		
		if (wordList.isEmpty()) {
			throw new ParseMessageExp(resourceBundle.getString("exception.salaryTransaction.parseError"));
		}
		//check length
		if (wordList.size() < 2) {
			throw new ParseMessageExp(resourceBundle.getString("exception.salaryTransaction.parseError"));
		}
		partner = new Partner(wordList.get(0)); 
		try {
			sumTransaction = new BigDecimal(wordList.get(1));
		}
		catch (NumberFormatException exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.salaryTransaction.parseError.sumIsIncorrect"));
		}
		
		if (wordList.size() > 2) {
			StringBuilder sb = new StringBuilder();
			wordList.stream().skip(2).forEach( w -> sb.append(w).append(" "));
			description = sb.toString().trim();
		}
	}
}
