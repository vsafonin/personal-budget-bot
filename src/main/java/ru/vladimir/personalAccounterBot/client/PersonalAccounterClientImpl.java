package ru.vladimir.personalAccounterBot.client;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.exception.DocumenIsExistExp;
import ru.vladimir.personalAccounterBot.exception.NeedUpdateTokenExcp;
import ru.vladimir.personalAccounterBot.exception.WrongEntityExcp;
import ru.vladimir.personalAccounterBot.model.DebtTransaction;
import ru.vladimir.personalAccounterBot.model.PurchaseTransaction;
import ru.vladimir.personalAccounterBot.model.SalaryTransaction;

@Component
public class PersonalAccounterClientImpl implements PersonalAccounterClient {
	@Value("${tg.bot.apiHost}")
	private String tgApiAddr;

	@Value("${tg.bot.apiScheme}")
	private String tgApiCheme;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Value("${personal.accounter.api.baseUrl}")
	private String baseUrl;
	
	@Value("${personal.accounter.api.scheme}")
	private String scheme;
	
	@Value("${personal.accounter.api.port}")
	private String portStr;

	@Override
	public void senddebtTransaction(TgUser tgUser, DebtTransaction debtTransaction)
			throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		sendToServer(objectMapper.writeValueAsString(debtTransaction), tgUser.getToken(),"debt");
	}
	

	@Override
	public void sendSalaryTransaction(TgUser tgUser, SalaryTransaction salaryTransaction)
			throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		sendToServer(objectMapper.writeValueAsString(salaryTransaction), tgUser.getToken(), "salary");
		
	}
	
	@Override
	public void sendShopTransaction(TgUser tgUser, PurchaseTransaction purchaseTransaction) throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		sendToServer(objectMapper.writeValueAsString(purchaseTransaction), tgUser.getToken(),"purchase-transaction");
	}

	private void sendToServer(String mappedObjct, String token, String pathSegment) throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		int port;
		Response response = null;
		try {
			port = Integer.parseInt(portStr);
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mappedObjct, mediaType);
			HttpUrl url = new HttpUrl.Builder()
					.scheme(scheme)
					.host(baseUrl)
					.port(port)
					.addPathSegment("api")
					.addPathSegment(pathSegment)
					.build();
			Request request = new Request.Builder().url(url).method("POST", body)
					.addHeader("Authorization", "Bearer " + token).addHeader("Content-Type", "application/json")
					.build();

			response = client.newCall(request).execute();
			if (response.code() == HttpStatus.SC_UNAUTHORIZED) {
				throw new NeedUpdateTokenExcp();
			}
			if (response.code() == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
				throw new WrongEntityExcp(response.body().string());
			}
			if(response.code() == HttpStatus.SC_CONFLICT) {
				throw new DocumenIsExistExp("already exists");
			}
			if (!response.body().contentType().equals(MediaType.parse("application/json"))) {
				throw new IOException("server response isn't json");
			}
		}
		catch (NumberFormatException exp) {
			throw new IllegalArgumentException("port in config is not int");
		}
		finally {
			if (response != null) response.close();
		}
		

		
	}







}
