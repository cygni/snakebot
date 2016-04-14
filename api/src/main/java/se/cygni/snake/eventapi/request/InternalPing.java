package se.cygni.snake.eventapi.request;

import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
@ApiMessageType
public class InternalPing extends ApiMessage {
    public InternalPing() {
    }
}
