package se.cygni.snake.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import se.cygni.snake.websocket.arena.ArenaWebSocketHandler;
import se.cygni.snake.websocket.event.EventSocketHandler;
import se.cygni.snake.websocket.tournament.TournamentWebSocketHandler;
import se.cygni.snake.websocket.training.TrainingWebSocketHandler;
import org.springframework.boot.autoconfigure.websocket.servlet.TomcatWebSocketServletWebServerCustomizer;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(eventWebSocketHandler(), "/events").setAllowedOrigins("*").withSockJS();
        registry.addHandler(eventWebSocketHandler(), "/events-native").setAllowedOrigins("*");
        registry.addHandler(snakeTrainingWebSocketHandler(), "/training");
        registry.addHandler(snakeTournamentWebSocketHandler(), "/tournament");
        registry.addHandler(snakeArenaWebSocketHandler(), "/arena", "/arena/", "/arena/{arenaName}");
    }

    @Bean
    public WebSocketHandler eventWebSocketHandler() {
        return new PerConnectionWebSocketHandler(EventSocketHandler.class, true);
    }

    @Bean
    public WebSocketHandler snakeTrainingWebSocketHandler() {
        return new PerConnectionWebSocketHandler(TrainingWebSocketHandler.class, true);
    }

    @Bean
    public WebSocketHandler snakeTournamentWebSocketHandler() {
        return new PerConnectionWebSocketHandler(TournamentWebSocketHandler.class, true);
    }

    @Bean
    public WebSocketHandler snakeArenaWebSocketHandler() {
        return new PerConnectionWebSocketHandler(ArenaWebSocketHandler.class, true);
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(512000);
        container.setMaxBinaryMessageBufferSize(512000);
        return container;
    }

    @Bean
    public TomcatWebSocketServletWebServerCustomizer tomcatWebSocketServletWebServerCustomizer() {
        return new TomcatWebSocketServletWebServerCustomizer();
    }

    @Bean
    public WebSocketContainerFactoryBean createWebSocketContainer() {
        WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
        container.setMaxTextMessageBufferSize(512000);
        container.setMaxBinaryMessageBufferSize(512000);
        return container;
    }
}
