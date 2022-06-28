/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.vladimir.personalAccounterBot.telegram;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author vladimir
 */
@Component
public class LongPoolingTgBot extends TelegramLongPollingBot {
    @Value("${tg.bot.name}")
    private String tgBotName;
    
    @Value("${tg.bot.token}")
    private String tgBotToken;

    @Autowired
    private MessageHandler messageHandler;
    
    private static final Logger LOG = Logger.getLogger(LongPoolingTgBot.class.getName());

    
    @Override
    public String getBotToken() {
        return tgBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
        	if (update.getMessage().getDocument() != null ) {
        		execute(messageHandler.answerMessage(update.getMessage(),update.getMessage().getDocument()));
        	}
        	else {
        		execute(messageHandler.answerMessage(update.getMessage()));
        	}
        }
        catch(TelegramApiException exp) {
            LOG.warning(">>>> tg has errors" + exp.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return tgBotName;
    }
    
}
