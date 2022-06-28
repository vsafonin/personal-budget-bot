package ru.vladimir.personalAccounterBot.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import ru.vladimir.personalAccounterBot.enums.CommandEnum;

@Component
public class TgKeyboard {
	
	protected ReplyKeyboardMarkup getTgKeyBoard(Locale tgLocale) {
		//create keyboard buttons
		KeyboardButton addShopTransaction = new KeyboardButton(CommandEnum.ADD_PURCHASE_TRANSACTION.getName(tgLocale));
		
		KeyboardButton addSalaryTransaction = new KeyboardButton(CommandEnum.ADD_SALARY_TRANSACTION.getName(tgLocale));
		
		KeyboardButton adddebtTransactionDecrease = new KeyboardButton(CommandEnum.ADD_DEBT_TRANSACTION_DECREASE.getName(tgLocale));
		
		KeyboardButton adddebtTransactionIncrease = new KeyboardButton(CommandEnum.ADD_DEBT_TRANSACTION_INCREASE.getName(tgLocale));
		
		KeyboardRow row1 = new KeyboardRow();
		row1.add(addShopTransaction);
		
		KeyboardRow row2 = new KeyboardRow();
		row2.add(addSalaryTransaction);
		
		KeyboardRow row3 = new KeyboardRow();
		row3.add(adddebtTransactionDecrease);
		row3.add(adddebtTransactionIncrease);
		
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(row1);
		keyboard.add(row2);
		keyboard.add(row3);
		
		
		return setTgKeyBoard(keyboard);
	}
	
	private ReplyKeyboardMarkup setTgKeyBoard(List<KeyboardRow> keyboardRow) {
	      final ReplyKeyboardMarkup replykeyboardMarkup = new ReplyKeyboardMarkup();
	        replykeyboardMarkup.setKeyboard(keyboardRow);
	        replykeyboardMarkup.setSelective(true);
	        replykeyboardMarkup.setResizeKeyboard(Boolean.TRUE);
	        replykeyboardMarkup.setOneTimeKeyboard(false);
	        return replykeyboardMarkup;
	}
}
