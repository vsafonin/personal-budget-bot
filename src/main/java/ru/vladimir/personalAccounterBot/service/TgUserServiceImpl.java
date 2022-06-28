package ru.vladimir.personalAccounterBot.service;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.vladimir.personalAccounterBot.entity.TgUser;
import ru.vladimir.personalAccounterBot.exception.TgUserNotFoundExp;
import ru.vladimir.personalAccounterBot.repository.TgUserRepository;

@Service
public class TgUserServiceImpl implements TgUserService {

    private static final Logger LOG = Logger.getLogger(TgUserServiceImpl.class.getName());

    @Value("${personal.accounter.api.baseUrl}") //see application.properties
    private String baseUrl;
    
	@Value("${personal.accounter.api.scheme}")
	private String scheme;
	
	@Value("${personal.accounter.api.port}")
	private String portStr;


    @Autowired
    private TgUserRepository tgUserRepository;

    @Override
    public TgUser findById(Long id) throws TgUserNotFoundExp {
        Optional<TgUser> theTgUserOptional = tgUserRepository.findById(id);
        if (theTgUserOptional.isPresent()) {
            return theTgUserOptional.get();
        } else {
            throw new TgUserNotFoundExp();
        }
    }

    @Override
    public TgUser saveUser(TgUser tgUser) {
        return  tgUserRepository.save(tgUser);
    }

    private String getUserNameFromDb(String token) throws IOException {
    	int port;
		try {
			port = Integer.parseInt(portStr);
		}
		catch (NumberFormatException exp) {
			throw new IllegalArgumentException("port in config is not int");
		}
		Response response = null;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            HttpUrl url = new HttpUrl.Builder()
            		.scheme(scheme)
            		.host(baseUrl)
            		.port(port)
            		.addPathSegment("api")
            		.addPathSegment("getUserName")
            		.build();
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            response = client.newCall(request).execute();
            if (response.code() == HttpStatus.SC_OK) {
                return response.body().string();
            } else if (response.code() == HttpStatus.SC_UNAUTHORIZED) {
                throw new IllegalArgumentException("token is unvalid");
            } else {
                throw new IllegalArgumentException("answer from server: " + response.code());
            }

        } catch (IOException exp) {
            LOG.warning(">>> personal accounter - main app is unvaible");
            throw new IOException(exp);
        }
        finally {
        	if (response != null) response.close();
		} 

    }

    @Override
    public TgUser createNewTgUser(Long chatId, String token) throws IOException,IllegalArgumentException{
        String userName = getUserNameFromDb(token);
        TgUser theTgUser = new TgUser();
        theTgUser.setName(userName);
        theTgUser.setToken(token);
        theTgUser.setId(chatId);
        return saveUser(theTgUser);        
    }

	@Override
	public void delete(TgUser tgUser) {
		tgUserRepository.delete(tgUser);
	}

}
