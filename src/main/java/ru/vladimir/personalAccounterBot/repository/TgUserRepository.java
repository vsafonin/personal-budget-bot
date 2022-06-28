/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ru.vladimir.personalAccounterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.vladimir.personalAccounterBot.entity.TgUser;

/**
 *
 * @author vladimir
 */
public interface TgUserRepository extends JpaRepository<TgUser, Long> {
    
}
