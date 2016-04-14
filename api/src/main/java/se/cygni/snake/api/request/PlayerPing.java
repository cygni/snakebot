package se.cygni.snake.api.request;

import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
@GameMessageType
public class PlayerPing extends GameMessage {
    public PlayerPing() {
    }
}
