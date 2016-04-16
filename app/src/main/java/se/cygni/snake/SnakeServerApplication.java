package se.cygni.snake;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebSocket
public class SnakeServerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {

        // If no active profile is set, default to development!
        if (StringUtils.isEmpty(System.getProperty("spring.profiles.active"))) {
            System.setProperty("spring.profiles.active", "development");
        }

        SpringApplication.run(SnakeServerApplication.class, args);
    }
}
