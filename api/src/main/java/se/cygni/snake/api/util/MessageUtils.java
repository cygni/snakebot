package se.cygni.snake.api.util;

import org.springframework.beans.BeanUtils;
import se.cygni.snake.api.GameMessage;

public class MessageUtils {

    public static <T extends GameMessage> T copyCommonAttributes(GameMessage src, T dst) {
        BeanUtils.copyProperties(src, dst);
        return dst;
    }
}
