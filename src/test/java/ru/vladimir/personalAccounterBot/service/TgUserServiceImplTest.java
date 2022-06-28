package ru.vladimir.personalAccounterBot.service;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.repository.TgUserRepository;

/**
 *
 * @author vladimir
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TgUserServiceImplTest {

    //this is real token for test user in main app
    //before start this test create user, get token and set his name
    private final String testUserName = "test";
    private final String realToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNjYyNDk0NDAwfQ.eNso11oEG-bwgIfV81cQH78QzvSp1bdhvXDCOSq9RHklWAkQulQ3-dhBc5Bathd8KLxELW2KurD1CqWYwkWXSA";

    @InjectMocks
    private TgUserServiceImpl tgUserServiceImpl;
    
    @MockBean
    private TgUserRepository tgUserRepository;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(tgUserServiceImpl, "baseUrl", "http://localhost:8080/api");
    }

    public TgUserServiceImplTest() {
    }

    @Test
    @Disabled
    public void testGetUserName() throws IOException {
    	ReflectionTestUtils.setField(tgUserServiceImpl, "portStr", "8080");
    	ReflectionTestUtils.setField(tgUserServiceImpl, "scheme", "http");
    	ReflectionTestUtils.setField(tgUserServiceImpl, "baseUrl", "localhost");
        Long chatId = 1L;
        TgUser theNewTgUser = new TgUser();
        theNewTgUser.setName(testUserName);
        Mockito.when(tgUserRepository.save(ArgumentMatchers.any(TgUser.class))).thenReturn(theNewTgUser);
        
        TgUser theTgUser = tgUserServiceImpl.createNewTgUser(chatId, realToken);
        
        Assertions.assertThat(theTgUser.getName()).isEqualTo(testUserName);
    }

}
