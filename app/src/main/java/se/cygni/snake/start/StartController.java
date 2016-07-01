package se.cygni.snake.start;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class StartController {

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws Exception {
        response.sendRedirect("http://game.snake.cygni.se");
    }
}
