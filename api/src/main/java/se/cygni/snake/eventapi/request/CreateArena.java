package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class CreateArena extends ApiMessage {
    
        private final String arenaName;
    
        @JsonCreator
        public CreateArena(
                @JsonProperty("arenaName") String arenaName) {
            System.out.println("CreateArenaGame constructor");
            this.arenaName = arenaName;
        }
    
        public String getArenaName() {
            return arenaName;
        }
}
