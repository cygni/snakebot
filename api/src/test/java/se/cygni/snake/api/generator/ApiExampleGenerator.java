package se.cygni.snake.api.generator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.GameAbortedEvent;
import se.cygni.snake.api.event.GameChangedEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ApiExampleGenerator {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) {
        generateExampleJson();
    }

    public static void generateExampleJson() {
        String destinationDir = ".";
        for (GameMessage gm : getGameMessages()) {
            try {
                write(destinationDir, gm.getClass().getSimpleName(), gm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void write(String destinationDirectory, String fileName, Object o) throws Exception {

        File file = new File(destinationDirectory, fileName + ".json.adoc");
        System.out.println(file.getAbsolutePath());
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            out.println("[[app-listing]]");
            out.println("[source,javascript]");
            out.println(fileName);
            out.println("----");
            out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o));
            out.println("----");
        }
    }

    public static List<GameMessage> getGameMessages() {
        List<GameMessage> messages = new ArrayList<>();
        messages.add(getGameAbortedEvent());
        messages.add(getGameChangedEvent());

        return messages;
    }

    public static GameAbortedEvent getGameAbortedEvent() {
        GameAbortedEvent gae = new GameAbortedEvent(getUUID());
        gae.setReceivingPlayerId(getUUID());
        gae.setTimestamp(getTimestamp());
        return gae;
    }

    public static GameChangedEvent getGameChangedEvent() {
        GameChangedEvent gce = new GameChangedEvent(getUUID());
        gce.setReceivingPlayerId(getUUID());
        gce.setTimestamp(getTimestamp());
        return gce;
    }


    private static long getTimestamp() {
        return new Date().getTime();
    }

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
