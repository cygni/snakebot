package se.cygni.snake.start;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

@RestController
public class StartController {

    @Value("${snakebot.redirect.url}")
    private String redirectUrl;

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws Exception {
        response.sendRedirect(redirectUrl);
    }
}
