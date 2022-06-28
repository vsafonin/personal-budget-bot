/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ru.vladimir.personalAccounterBot.service;

import java.io.IOException;

import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.exception.TgUserNotFoundExp;

/**
 *
 * @author vladimir
 */
public interface TgUserService {
    
    TgUser findById(Long id) throws TgUserNotFoundExp;
    
    TgUser saveUser(TgUser tgUser) throws IOException,IllegalArgumentException;

    public TgUser createNewTgUser(Long chatId, String token) throws IOException,IllegalArgumentException;

	void delete(TgUser tgUser);
}
