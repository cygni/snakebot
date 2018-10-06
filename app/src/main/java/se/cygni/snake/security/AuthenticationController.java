package se.cygni.snake.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(
            AuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> authenticate(
            @RequestParam(value="login") String login,
            @RequestParam(value="password") String password) {

        try {
            String token = authenticationService.authenticate(login, password);
            return new ResponseEntity<String>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
