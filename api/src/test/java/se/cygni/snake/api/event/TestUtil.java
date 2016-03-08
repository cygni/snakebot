package se.cygni.snake.api.event;

import se.cygni.snake.api.GameMessage;

public class TestUtil {

    public static void populateBaseData(GameMessage message, String receivingPlayerId) {
        message.setReceivingPlayerId(receivingPlayerId);
    }
}
