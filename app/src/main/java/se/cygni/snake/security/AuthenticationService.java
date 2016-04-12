package se.cygni.snake.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    TokenService tokenService;

    Map<String, String> users = new HashMap<String, String>() {{
        put("emil", "lime");
        put("chen", "nehc");
        put("johannes", "sennahoj");
    }};

    @Autowired
    public AuthenticationService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String authenticate(String login, String password) throws Exception {
        if (users.containsKey(login)) {
            String pwd = users.get(login);

            if (pwd.equals(password)) {
                return tokenService.createToken();
            }
        }

        throw new Exception("Invalid credentials");
    }
}
