/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.vladimir.personalAccounterBot.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author vladimir
 */

@Entity
@Table(name = "tg_user")
@Getter
@Setter
@NoArgsConstructor
public class TgUser implements Serializable{

    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "token")
    private String token;
    
}
