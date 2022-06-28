package ru.vladimir.personalAccounterBot.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vladimir.personalAccounterBot.enums.TypeOfOperation;
import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;
import ru.vladimir.personalAccounterBot.util.ParseLineUtil;

//i want create this class from this message
//pupa 400 09.06.2099
@Getter
@Setter
@NoArgsConstructor
public class DebtTransaction {
	
	private Partner partner;
	
	private BigDecimal sumTransaction;
	
	private LocalDate endDate;
	
	private TypeOfOperation typeOfOperation;
	
	
	public DebtTransaction(String line,@Nullable Locale locale, TypeOfOperation typeOfOperation) throws ParseMessageExp {
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		parseFromString(line, locale);
		this.typeOfOperation = typeOfOperation;
	}
	
	private void parseFromString(String lineOfString, Locale locale ) throws ParseMessageExp {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",locale);
		
		List<String> wordList = new ArrayList<String>();
		try {
			wordList	= ParseLineUtil.parse(lineOfString);
		}
		catch (ParseMessageExp exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.debtTransaction.parseError"));
		}
		if (wordList.isEmpty()) {
			throw new ParseMessageExp(resourceBundle.getString("exception.debtTransaction.parseError"));
		}
		//check length
		if (wordList.size() != 3) {
			throw new ParseMessageExp(resourceBundle.getString("exception.debtTransaction.parseError"));
		}
		partner = new Partner(wordList.get(0)); 
		try {
			sumTransaction = new BigDecimal(wordList.get(1));
		}
		catch (NumberFormatException exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.debtTransaction.parseError.sumIsIncorrect"));
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			endDate = LocalDate.parse(wordList.get(2),formatter);
		}
		catch (DateTimeParseException exp) {
			throw new ParseMessageExp(resourceBundle.getString("exception.debtTransaction.parseError.dateIsIncorrect"));
		}
		
	}
}
