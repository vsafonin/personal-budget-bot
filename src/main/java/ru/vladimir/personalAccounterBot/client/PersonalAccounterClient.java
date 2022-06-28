package ru.vladimir.personalAccounterBot.client;

import java.io.IOException;

import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.exception.DocumenIsExistExp;
import ru.vladimir.personalAccounterBot.exception.NeedUpdateTokenExcp;
import ru.vladimir.personalAccounterBot.exception.WrongEntityExcp;
import ru.vladimir.personalAccounterBot.model.DebtTransaction;
import ru.vladimir.personalAccounterBot.model.PurchaseTransaction;
import ru.vladimir.personalAccounterBot.model.SalaryTransaction;

public interface PersonalAccounterClient {
	
	void senddebtTransaction(TgUser tgUser ,DebtTransaction debtTransactionDecrease) throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp;

	void sendSalaryTransaction(TgUser tgUser, SalaryTransaction salaryTransaction) throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp;

	void sendShopTransaction(TgUser tgUser, PurchaseTransaction purchaseTransaction) throws IOException, NeedUpdateTokenExcp, DocumenIsExistExp, WrongEntityExcp;
	
}
