package se.cygni.snake.eventapi;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import se.cygni.snake.eventapi.type.ApiMessageType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ApiMessageParser {
    private static Logger log = LoggerFactory.getLogger(ApiMessageParser.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private static JsonFactory factory = new JsonFactory();
    private static final String TYPE_IDENTIFIER = "type";

    private static Map<String, Class<? extends ApiMessage>> typeToClass = new HashMap<>();

    // Init typeToClass map
    static {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ApiMessageType.class));

        for (final BeanDefinition bd : scanner
                .findCandidateComponents("se.cygni.snake.eventapi")) {
            try {
                typeToClass.put(bd.getBeanClassName(),
                        (Class<? extends ApiMessage>) Class.forName(bd
                                .getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                log.warn("Error in caching class in Type to Class map", e);
            }
        }

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private ApiMessageParser() {
    }

    public static ApiMessage decodeMessage(final String msg)
            throws IOException {
        try {
            return mapper
                    .readValue(msg,
                            ApiMessageParser.parseAndGetClassForMessage(msg));
        } catch (final IllegalStateException e) {
            log.error(msg);
            throw e;
        }
    }

    public static String encodeMessage(final ApiMessage message)
            throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, message);
        return out.toString();
    }

    public static Class<? extends ApiMessage> parseAndGetClassForMessage(
            final String message) {

        JsonParser parser = null;
        try {
            parser = factory.createParser(message);
            final JsonNode node = mapper.readTree(parser);
            final JsonNode typeNode = node.path(TYPE_IDENTIFIER);

            if (typeNode == null
                    || StringUtils.isEmpty(typeNode.asText()))
            // Nothing found
            {
                throw new IllegalArgumentException("Could not find [" +
                        TYPE_IDENTIFIER
                        + "] in message: " + message);
            }

            return getClassForIdentifier(typeNode.asText());

        } catch (final Exception e) {
            // JSON exception
            throw new IllegalArgumentException("Could not parse message: "
                    + message, e);
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (final IOException e) {
                    log.info("Failed to close json parser");
                }
            }
        }
    }

    public static Class<? extends ApiMessage> getClassForIdentifier(
            final String identifier) {

        if (typeToClass.containsKey(identifier)) {
            return typeToClass.get(identifier);
        }

        throw new IllegalArgumentException("Unknown identifier: " + identifier);
    }
}
