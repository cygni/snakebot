package se.cygni.snake.security;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class TokenService {

    Set<String> tokens = new HashSet<>();

    public String createToken() {
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        return token;
    }

    public boolean isTokenValid(String token) {
        // return true;
        return tokens.contains(token);
    }
}
