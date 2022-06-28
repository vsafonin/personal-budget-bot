package ru.vladimir.personalAccounterBot.telegram;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.vladimir.personalAccounterBot.client.PersonalAccounterClient;
import ru.vladimir.personalAccounterBot.client.TelegramApiClient;
import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.enums.CommandEnum;
import ru.vladimir.personalAccounterBot.enums.TypeOfOperation;
import ru.vladimir.personalAccounterBot.exception.ComandEnumNotFoundExp;
import ru.vladimir.personalAccounterBot.exception.DocumenIsExistExp;
import ru.vladimir.personalAccounterBot.exception.NeedUpdateTokenExcp;
import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;
import ru.vladimir.personalAccounterBot.exception.TgUserNotFoundExp;
import ru.vladimir.personalAccounterBot.exception.WrongAnswerFromServer;
import ru.vladimir.personalAccounterBot.exception.WrongEntityExcp;
import ru.vladimir.personalAccounterBot.model.DebtTransaction;
import ru.vladimir.personalAccounterBot.model.PurchaseTransaction;
import ru.vladimir.personalAccounterBot.model.SalaryTransaction;
import ru.vladimir.personalAccounterBot.service.TgUserService;

/**
 *
 * @author vladimir
 */
@Component
public class MessageHandler {

	@Autowired
	private TgUserService tgUserService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private TgKeyboard tgKeyboard;

	@Autowired
	private PersonalAccounterClient personalAcctounerClient;
	
	@Autowired
	private TelegramApiClient telegramApiClient;

	// this i use for save first message, and if it from command button save it, and
	// wait second message
	private Map<Long, CommandEnum> cacheForWaitSecondMessage = new HashMap<>();

	public SendMessage answerMessage(Message message, Document document) {
		// if we have document we cat read informarion from here
		// check user is registered
		Long chatId = message.getChatId();
		Locale tgUserLocale = new Locale(message.getFrom().getLanguageCode());
		TgUser tgUser;
		try {
			tgUser = getTgUser(chatId);
		} catch (TgUserNotFoundExp exp) {
			String localizedMessage = messageSource.getMessage("tg.need.register", null,tgUserLocale);
			return new SendMessage(chatId.toString(),localizedMessage);
		}
		
		if (!document.getFileName().endsWith(".json")) { //i don't know why, but wen i send this json from nalog app, return plain text 
			String localizedMessage = messageSource.getMessage("exception.purchaseTransaction.parseError.json", null, tgUserLocale);
			return getMessageWithKeyBoard(chatId, localizedMessage, tgUserLocale);
		}
		else {
			try {
				JSONObject jsObject = telegramApiClient.getJsonFromTg(document);
				PurchaseTransaction purchaseTransaction = new PurchaseTransaction(jsObject);
				personalAcctounerClient.sendShopTransaction(tgUser, purchaseTransaction);
				return getSuccessMessages(chatId, tgUserLocale);
			}
			catch (NeedUpdateTokenExcp exp) {
				//delete user from db
				tgUserService.delete(tgUser);
				return getMessageWithKeyBoard(chatId, exp.getMessage(), tgUserLocale);
			}
			catch (IOException  | WrongAnswerFromServer | JSONException | ParseMessageExp | DocumenIsExistExp | WrongEntityExcp  exp) {
				return getMessageWithKeyBoard(chatId, exp.getMessage(), tgUserLocale);
			}
			
		}

	}

	public SendMessage answerMessage(Message message) throws DocumenIsExistExp {
		// get chatid
		Long chatId = message.getChatId();
		Locale tgUserLocale = new Locale(message.getFrom().getLanguageCode());

		String textFromTg = message.getText();
		// check maybe this is registration
		if (textFromTg.startsWith(CommandEnum.REGISTER.getName(tgUserLocale))) {
			// send registr message
			String[] words = textFromTg.split(" ");
			if (words.length != 2) {
				String localizedMessage = messageSource.getMessage("tg.need.register.wrongParametr", null,tgUserLocale);
				return new SendMessage(chatId.toString(),localizedMessage);
			} else {
				return createUser(chatId, words[1], tgUserLocale);
			}
		}
		// check user is registered
		TgUser tgUser;
		try {
			tgUser = getTgUser(chatId);
		} catch (TgUserNotFoundExp exp) {
			String localizedMessage = messageSource.getMessage("tg.need.register", null,tgUserLocale);
			return new SendMessage(chatId.toString(),localizedMessage);
		}
		
		if (message.getText().equals(CommandEnum.START_BUTTON.getName(tgUserLocale))) {
			String localizedMessage = messageSource.getMessage("info.operation.START_BUTTON",null ,tgUserLocale);
			return getMessageWithKeyBoard(chatId, localizedMessage, tgUserLocale);
		}

		// check what is user say
		CommandEnum button = getComandEnumFromText(textFromTg, tgUserLocale); // if not start in our command button -
																				// throw exception and do another logic
		try {
			if (button != null) {
		
				if (registerFirstUserMessage(tgUser, textFromTg, tgUserLocale)) {
					// check may be this is second message
					return getInfoMessages(tgUser.getId(), textFromTg, tgUserLocale);
				} else {
					// return success
					return getSuccessMessages(tgUser.getId(), tgUserLocale);
				}
			} else {
				
				if (cacheForWaitSecondMessage.containsKey(tgUser.getId())) {
					parseSecondMessage(tgUser, textFromTg, cacheForWaitSecondMessage.get(tgUser.getId()), tgUserLocale);
					// remove from map
					cacheForWaitSecondMessage.remove(tgUser.getId());
					return getSuccessMessages(tgUser.getId(), tgUserLocale); // if not we got exception (i
																						// believe it)

				} else {
				
					return getMessageWithKeyBoard(chatId, messageSource.getMessage("exception.parseLine", null, tgUserLocale),
							tgUserLocale);
				}
			}
		} 
		catch (NeedUpdateTokenExcp exp) {
			//delete user
			tgUserService.delete(tgUser);
			return getMessageWithKeyBoard(chatId, exp.getMessage(),tgUserLocale);
		}
		catch (ParseMessageExp | IOException  | ComandEnumNotFoundExp | WrongEntityExcp e) {
			StringBuilder sb = new StringBuilder();
			sb.append(e.getMessage());
			if (button != null) {
				sb.append("\n");
				sb.append(messageSource.getMessage("info.operation." + button.toString(), null, tgUserLocale));

			}
			return getMessageWithKeyBoard(chatId, sb.toString(),tgUserLocale);
		}

	}
	
	private SendMessage getMessageWithKeyBoard(Long chatId, String message,Locale tgUserLocale) {
		SendMessage sendMessage = new SendMessage(chatId.toString(),
				message);
		sendMessage.setReplyMarkup(tgKeyboard.getTgKeyBoard(tgUserLocale));
		return sendMessage;
	}

	private TgUser getTgUser(Long chatId) throws TgUserNotFoundExp {
		return tgUserService.findById(chatId);

	}

	private SendMessage getSuccessMessages(Long chatId, Locale tgUserLocale) {
		String localizedMessage = messageSource.getMessage("info.success.message", null, tgUserLocale);
		return getMessageWithKeyBoard(chatId, localizedMessage, tgUserLocale);
	}

	private SendMessage getInfoMessages(Long chatId, String textFromTg, Locale tgUserLocale)
			throws ComandEnumNotFoundExp {
		CommandEnum button = getComandEnumFromText(textFromTg, tgUserLocale);
		if (button != null) {
			String localizedMessage = messageSource.getMessage("info.operation." + button, null, tgUserLocale); 
			return getMessageWithKeyBoard(chatId, localizedMessage, tgUserLocale);			
		} else {
			throw new ComandEnumNotFoundExp(messageSource.getMessage("exception.comandButton", null, tgUserLocale));
		}

	}

	private boolean registerFirstUserMessage(TgUser tgUser, String textFromTg, Locale tgUserLocale)
			throws ParseMessageExp, IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {

		boolean registred = false;

		if (textFromTg.startsWith(CommandEnum.ADD_PURCHASE_TRANSACTION.getName(tgUserLocale))) {
			if (addToChache(textFromTg, CommandEnum.ADD_PURCHASE_TRANSACTION, tgUser, tgUserLocale)) {
				registred = true;
			} else {
				addShopTransaction(tgUser,
						textFromTg.substring(CommandEnum.ADD_PURCHASE_TRANSACTION.getName(tgUserLocale).length() + 1),
						tgUserLocale);
			}

		} else if (textFromTg.startsWith(CommandEnum.ADD_DEBT_TRANSACTION_INCREASE.getName(tgUserLocale))) {
			if (addToChache(textFromTg, CommandEnum.ADD_DEBT_TRANSACTION_INCREASE, tgUser, tgUserLocale)) {
				registred = true;

			} else {
				adddebtTransactionIncrease(tgUser,
						textFromTg.substring(
								CommandEnum.ADD_DEBT_TRANSACTION_INCREASE.getName(tgUserLocale).length() + 1),
						tgUserLocale);
			}

		} else if (textFromTg.startsWith(CommandEnum.ADD_DEBT_TRANSACTION_DECREASE.getName(tgUserLocale))) {
			if (addToChache(textFromTg, CommandEnum.ADD_DEBT_TRANSACTION_DECREASE, tgUser, tgUserLocale)) {
				registred = true;

			} else {
				adddebtTransactionDecrease(tgUser,
						textFromTg.substring(
								CommandEnum.ADD_DEBT_TRANSACTION_DECREASE.getName(tgUserLocale).length() + 1),
						tgUserLocale);
			}

		} else if (textFromTg.startsWith(CommandEnum.ADD_SALARY_TRANSACTION.getName(tgUserLocale))) {
			if (addToChache(textFromTg, CommandEnum.ADD_SALARY_TRANSACTION, tgUser, tgUserLocale)) {
				registred = true;
			} else {
				addSalaryTransaction(tgUser,
						textFromTg.substring(CommandEnum.ADD_SALARY_TRANSACTION.getName(tgUserLocale).length() + 1),
						tgUserLocale);

			}

		} else {
			throw new ParseMessageExp(messageSource.getMessage("exception.parseLine", null, tgUserLocale));
		}

		return registred;
	}

	private void parseSecondMessage(TgUser tgUser, String textFromUser, CommandEnum button, Locale tgUserLocale)
			throws ParseMessageExp, IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		if (button == CommandEnum.ADD_DEBT_TRANSACTION_DECREASE) {
			adddebtTransactionDecrease(tgUser, textFromUser, tgUserLocale);
		} else if (button == CommandEnum.ADD_DEBT_TRANSACTION_INCREASE) {
			adddebtTransactionIncrease(tgUser, textFromUser, tgUserLocale);
		} else if (button == CommandEnum.ADD_SALARY_TRANSACTION) {
			addSalaryTransaction(tgUser, textFromUser, tgUserLocale);
		} else if (button == CommandEnum.ADD_PURCHASE_TRANSACTION) {
			addShopTransaction(tgUser, textFromUser, tgUserLocale);
		} else {
			throw new ParseMessageExp(messageSource.getMessage("exception.unknown", null, tgUserLocale));
		}
	}

	private void adddebtTransactionIncrease(TgUser tgUser, String textFromUser, Locale tgUserLocale)
			throws IOException, NeedUpdateTokenExcp, ParseMessageExp, DocumenIsExistExp, WrongEntityExcp {
		DebtTransaction debtTransactionIncrease = new DebtTransaction(textFromUser, tgUserLocale,
				TypeOfOperation.INCREASE);
		personalAcctounerClient.senddebtTransaction(tgUser, debtTransactionIncrease);
	}

	private void addSalaryTransaction(TgUser tgUser, String textFromUser, Locale tgUserLocale)
			throws ParseMessageExp, IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		SalaryTransaction salaryTransaction = new SalaryTransaction(textFromUser, tgUserLocale);
		personalAcctounerClient.sendSalaryTransaction(tgUser, salaryTransaction);

	}

	private void adddebtTransactionDecrease(TgUser tgUser, String textFromUser, Locale tgUserLocale)
			throws ParseMessageExp, IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {

		DebtTransaction debtTransactionDecrease = new DebtTransaction(textFromUser, tgUserLocale,
				TypeOfOperation.DECREASE);
		personalAcctounerClient.senddebtTransaction(tgUser, debtTransactionDecrease);
	}

	private void addShopTransaction(TgUser tgUser, String userText, Locale tgUserLocale)
			throws ParseMessageExp, IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp {
		PurchaseTransaction purchaseTransaction = new PurchaseTransaction(userText, tgUserLocale);
		personalAcctounerClient.sendShopTransaction(tgUser, purchaseTransaction);

	}

	private boolean addToChache(String textFromTg, CommandEnum button, TgUser tgUser, Locale tgUserlocale) {
		boolean isAdded = false;
		// add purchase transaction
		String lineWithOutButton = textFromTg.substring(button.getName(tgUserlocale).length());
		if (lineWithOutButton.split("[ ,.]").length == 1) {
			cacheForWaitSecondMessage.put(tgUser.getId(), getComandEnumFromText(textFromTg, tgUserlocale));
			isAdded = true;
		}
		return isAdded;
	}

	private CommandEnum getComandEnumFromText(String nameEnum, Locale tgUserLocale) {
		CommandEnum commandEnum = null;
		if (nameEnum.startsWith(CommandEnum.ADD_DEBT_TRANSACTION_DECREASE.getName(tgUserLocale))) {
			commandEnum = CommandEnum.ADD_DEBT_TRANSACTION_DECREASE;
		}
		if (nameEnum.startsWith(CommandEnum.ADD_DEBT_TRANSACTION_INCREASE.getName(tgUserLocale))) {
			commandEnum = CommandEnum.ADD_DEBT_TRANSACTION_INCREASE;
		}
		if (nameEnum.startsWith(CommandEnum.ADD_SALARY_TRANSACTION.getName(tgUserLocale))) {
			commandEnum = CommandEnum.ADD_SALARY_TRANSACTION;
		}
		if (nameEnum.startsWith(CommandEnum.ADD_PURCHASE_TRANSACTION.getName(tgUserLocale))) {
			commandEnum = CommandEnum.ADD_PURCHASE_TRANSACTION;
		}

		return commandEnum;
	}

	private SendMessage createUser(Long chatId, String token, Locale locale) {
		SendMessage theSendMessage = new SendMessage();

		theSendMessage.setChatId(chatId.toString());
		try {
			TgUser theTgUser = tgUserService.createNewTgUser(chatId, token);
			StringBuilder builder = new StringBuilder();
			builder.append(messageSource.getMessage("tg.register.success", null, locale));
			builder.append(theTgUser.getName()).append("!");
			theSendMessage.setText(builder.toString());
			theSendMessage.setReplyMarkup(tgKeyboard.getTgKeyBoard(locale));
		} catch (IllegalArgumentException exp) {
			theSendMessage.setText(exp.getMessage());
		} catch (IOException exp) {
			theSendMessage.setText("service not work, we will fix it soon");
		}
		return theSendMessage;
	}
	
	

}
