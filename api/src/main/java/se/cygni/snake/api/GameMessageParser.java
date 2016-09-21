package se.cygni.snake.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unchecked")
public class GameMessageParser {
    private static Logger log = LoggerFactory.getLogger(GameMessageParser.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
    }

    private GameMessageParser() {
    }

    public static GameMessage decodeMessage(final String msg)
            throws IOException {
        try {
            return mapper
                    .readValue(msg, GameMessage.class);
        } catch (final IllegalStateException e) {
            throw e;
        }
    }

    public static String encodeMessage(final GameMessage message)
            throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, message);
        return out.toString();
    }
}
