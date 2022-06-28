package ru.vladimir.personalAccounterBot.client;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.vladimir.personalAccounterBot.exception.NeedUpdateTokenExcp;
import ru.vladimir.personalAccounterBot.exception.WrongAnswerFromServer;

@Component
public class TelegramApiClientImpl implements TelegramApiClient {

	@Value("${tg.bot.token}")
	private String tgBotToken;

	@Value("${tg.bot.apiScheme}")
	private String tgApiScheme;

	@Value("${tg.bot.apiHost}")
	private String tgApiHost;

	@Override
	public JSONObject getJsonFromTg(Document document) throws IOException, NeedUpdateTokenExcp, WrongAnswerFromServer {
		String filePath = getFileIdFromTg(document.getFileId());
		return getFile(filePath);
	}

	private String getFileIdFromTg(String fileId) throws IOException, WrongAnswerFromServer {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		HttpUrl url = new HttpUrl.Builder().scheme(tgApiScheme).host(tgApiHost).addPathSegment("bot" + tgBotToken)
					.addPathSegment("getFile").addQueryParameter("file_id", fileId).build();
		

		Request request = new Request.Builder().url(url).method("GET", null).build();
		try (Response response = client.newCall(request).execute();) {
			if (response.code() == HttpStatus.SC_OK) {
				JSONObject jsonObject = new JSONObject(response.body().string());
				return jsonObject.getJSONObject("result").getString("file_path");

			}
			else {
				throw new WrongAnswerFromServer("telegram api send wrong response, with code: " + response.code());
			}
		}
	}
	
	private  JSONObject getFile(String filePath) throws IOException, WrongAnswerFromServer {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		HttpUrl url = new HttpUrl.Builder().scheme(tgApiScheme).host(tgApiHost)
				.addPathSegment("file")
				.addPathSegment("bot" + tgBotToken)
				.addPathSegment(filePath)
				.build();

		Request request = new Request.Builder().url(url).method("GET", null).build();
		try (Response response = client.newCall(request).execute();) {
			if (response.code() == HttpStatus.SC_OK) {
				JSONObject jsonObject = new JSONObject(response.body().string());
				return jsonObject;

			}
			else {
				throw new WrongAnswerFromServer("telegram api send wrong response, with code: " + response.code());
			}
		}
	}

}
