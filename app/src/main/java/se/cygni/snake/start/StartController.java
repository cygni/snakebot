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

    @RequestMapping("/mtd2017")
    public void mtd2017(HttpServletResponse response) throws Exception {
        response.sendRedirect("https://docs.google.com/a/cygni.se/forms/d/e/1FAIpQLSe0tZDPLPGvj05wiC57LzieVOv3Y_s26fHtJ6S0KVViLbpUBw/viewform");
    }
}
