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
class MessageHandlerTestShopTransaction {

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
	void testAddShopFirstMessageShouldBeOk() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.operation.ADD_PURCHASE_TRANSACTION"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testAddShopFirstMessageShouldBeFailWrnongNumberOfWords() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction test sa");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't parse line to purchase transaction"); 
	}

	
	@Test
	void testAddShopFirstMessageShouldBeFailWrongSum() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction test sls 09.06.2022");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).contains("I can't make out the line in the add purchase operation.The sum is not correct"); 
	}
	
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction test test 200 1 products");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line_Without_category() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction test test 200 1");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line_Without_quantity_with_category() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction test test 200 products");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line_double_qouteShop() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction \"test 2\" test 200 products");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line_double_qouteShop_andProduct() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction \"test 2\" \"test 2\" 200 products");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	@Test
	void testAddShopFirstMessageShouldBeOk_in_one_line_double_qouteShop_andProduct_withoutSpace() throws TgUserNotFoundExp, IllegalArgumentException, IOException {
		Message message = mock(Message.class);
		when(message.getChatId()).thenReturn(1L);
		User user = mock(User.class);
		when(message.getFrom()).thenReturn(user);
		when(user.getLanguageCode()).thenReturn(Locale.ENGLISH.toLanguageTag());
		String tempToken = "sasdalasda";
		when(message.getText()).thenReturn("Add purchase transaction \"test2\"\"test 2\"200 products");
		when(tgUserService.createNewTgUser(1L, tempToken)).thenReturn(new TgUser());
		when(messageSource.getMessage(eq("info.success.message"), any(), any())).thenReturn("success");
		
		TgUser theTgUser = new TgUser();
		theTgUser.setName("test");
		when(tgUserService.findById(1L)).thenReturn(theTgUser);
		
		SendMessage sendMessage = messageHandler.answerMessage(message);
		
		
		assertThat(sendMessage.getText()).isEqualTo("success"); 
	}
	

}
