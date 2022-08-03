package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class CreateArena extends ApiMessage {
    
        private GameSettings gameSettings;
    
        @JsonCreator
        public CreateArena(
                @JsonProperty("gameSettings") GameSettings gameSettings) {
            this.gameSettings = gameSettings;
        }

        public GameSettings getGameSettings() {
            return gameSettings;
        }
}
