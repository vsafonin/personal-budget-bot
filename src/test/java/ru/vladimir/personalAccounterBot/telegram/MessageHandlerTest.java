package ru.vladimir.personalAccounterBot.telegram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import ru.vladimir.personalAccounterBot.client.PersonalAccounterClient;
import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.exception.TgUserNotFoundExp;
import ru.vladimir.personalAccounterBot.service.TgUserService;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

	@InjectMocks
	private MessageHandler messageHandler;
	
	@MockBean 
	private MessageSource messageSource;
	
	@MockBean
	private TgUserService tgUserService;
	
	@MockBean
	private TgKeyboard tgKeyboard;
	
	
	@MockBean
	private PersonalAccounterClient personalAccounterClient;
	
	@Test
	void testUnregistredUserShouldBeGetMessage() throws TgUserNotFoundExp {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn("en");
		when(message.getText()).thenReturn("sada");
		when(messageSource.getMessage(eq("tg.need.register"), any(), any())).thenReturn("You need register");
		when(tgUserService.findById(1L)).thenThrow(TgUserNotFoundExp.class);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		assertThat(sendMessage.getText()).isEqualTo("You need register");
	}
	@Test
	void testRegistredIllegalArgumentBeGetMessage() throws TgUserNotFoundExp {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn("en");
		when(message.getText()).thenReturn("/register sda sad");
		when(messageSource.getMessage(eq("tg.need.register.wrongParametr"), any(), any())).thenReturn("You need register");
		when(tgUserService.findById(1L)).thenThrow(TgUserNotFoundExp.class);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		assertThat(sendMessage.getText()).isEqualTo("You need register");
	}
	@Test
	void testRegistredIllegalArgument2BeGetMessage() throws TgUserNotFoundExp {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn("en");
		when(message.getText()).thenReturn("/register");
		when(messageSource.getMessage(eq("tg.need.register.wrongParametr"), any(), any())).thenReturn("You need register");
		when(tgUserService.findById(1L)).thenThrow(TgUserNotFoundExp.class);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		assertThat(sendMessage.getText()).isEqualTo("You need register");
	}
	@Test
	void testRegistrShouldBeOk() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn("en");
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("/register " + tempToken);
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("tg.register.success"), any(), any())).thenReturn("success ");
		when(tgUserService.findById(1L)).thenThrow(TgUserNotFoundExp.class);

		SendMessage sendMessage = messageHandler.answerMessage(message);
		assertThat(sendMessage.getText()).isEqualTo("success null!"); //cause we use success + username!
	}
	
	@Test
	void testaddDebtDecreaseFirstMessageShouldBeOk() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add lend");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.operation.ADD_DEBT_TRANSACTION_DECREASE"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testaddDebtIncreaseFirstMessageShouldBeOk() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add borrow");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.operation.ADD_DEBT_TRANSACTION_INCREASE"), any(), any())).thenReturn("info");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		assertThat(sendMessage.getText()).isEqualTo("info"); 
	}
	
	@Test
	void testaddDebtDecreaseFirstMessageShouldBeFailWrnongNumberOfWords() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add lend skd sl");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtIncreaseFirstMessageShouldBeFailWrnongNumberOfWords() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add borrow skd sl");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtDecreaseFirstMessageShouldBeFailWrongSum() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add lend test sls 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.operation.ADD_CREDIT_TRANSACTION_DECREASE"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtIncreaseFirstMessageShouldBeFailWrongSum() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add borrow test sls 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		//when(messageSource.getMessage(eq("info.operation.ADD_CREDIT_TRANSACTION_DECREASE"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtDecreaseFirstMessageShouldBeFailDate() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add lend test 200 09/06/2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtIncreaseFirstMessageShouldBeFailDate() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add borrow test 200 09/06/2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to debt transaction"); 
	}
	
	@Test
	void testaddDebtDecreaseFirstMessageShouldBeOk_in_one_line() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add lend test 200 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testaddDebtIncreaseFirstMessageShouldBeOk_in_one_line() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add borrow \"test and do\" 200 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}

}
