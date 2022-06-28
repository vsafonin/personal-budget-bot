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
class MessageHandlerTestSalaryTransaction {

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
	void testAddSalaryeFirstMessageShouldBeOk() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.operation.ADD_SALARY_TRANSACTION"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testAddSalaryFirstMessageShouldBeFailWrnongNumberOfWords() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction test sa 500 20.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to salary transaction. Sum is incorrect"); 
	}

	@Test
	void testAddSalaryFirstMessageShouldBeFailWrongSum() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction test sls 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to salary transaction"); 
	}
	

	
	
	@Test
	void testAddSalaryFirstMessageShouldBeOk_in_one_line() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction test 200");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testAddSalaryFirstMessageShouldBeOk_in_one_line_With_description() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction test 200 pay for work");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testAddSalaryFirstMessageShouldBeOk_in_one_line_With_partner_double_quote() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction \"test pupa\" 200");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testAddSalaryFirstMessageShouldBeOk_in_one_line_With_partner_double_quote_and_description() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add salary transaction \"test pupa\" 200 kjalk kjlakjldalk jas");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}

}
