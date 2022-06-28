/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ru.vladimir.personalAccounterBot.enums;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author vladimir
 */
public enum CommandEnum {
 
    REGISTER("register"),ADD_PURCHASE_TRANSACTION("addPurchaseTransaction"), ADD_SALARY_TRANSACTION("addSalaryTransaction"), 
    ADD_DEBT_TRANSACTION_DECREASE("addDebtDecrease"), ADD_DEBT_TRANSACTION_INCREASE("addDebtIncrease"),
    START_BUTTON("start");
    
    private String name;

    CommandEnum(String name) {
    
        this.name = name;
    }
    
    public String getName(Locale locale) {
    	//
 		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",locale);
	
		String resultName = resourceBundle.getString("keyboard.button." + this.name);
		return resultName;
    }
    
    
}
