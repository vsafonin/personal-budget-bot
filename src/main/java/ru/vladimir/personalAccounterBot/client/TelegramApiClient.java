package ru.vladimir.personalAccounterBot.client;

import java.io.IOException;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Document;

import ru.vladimir.personalAccounterBot.exception.NeedUpdateTokenExcp;
import ru.vladimir.personalAccounterBot.exception.WrongAnswerFromServer;

public interface TelegramApiClient {
	
	JSONObject getJsonFromTg(Document document) throws IOException,NeedUpdateTokenExcp, WrongAnswerFromServer;
}	
