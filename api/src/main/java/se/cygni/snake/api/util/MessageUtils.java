package se.cygni.snake.api.util;

import org.springframework.beans.BeanUtils;
import se.cygni.snake.api.GameMessage;

import java.lang.reflect.Method;
import java.util.Optional;

public class MessageUtils {

    public static <T extends GameMessage> T copyCommonAttributes(GameMessage src, T dst) {
        BeanUtils.copyProperties(src, dst);
        return dst;
    }

    public static Optional<String> extractGameId(GameMessage message) {

        try {
            Method m = BeanUtils.findDeclaredMethod(message.getClass(), "getGameId", null);
            if (m != null) {
                return Optional.of((String) m.invoke(message));
            }
        } catch (Exception e) {
            // It's okay
        }

        return Optional.empty();
    }
}
