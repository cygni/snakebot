package se.cygni.snake.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import se.cygni.snake.api.model.*;

import java.io.IOException;

public class TileContentDeserializer extends JsonDeserializer<TileContent> {
    @Override
    public TileContent deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        JsonNode node = jp.getCodec().readTree(jp);

        String content = node.get("content").asText();

        String name = "";
        if (node.has("name"))
            name = node.get("name").asText();

        String playerId = "";
        if (node.has("playerId"))
            playerId = node.get("playerId").asText();

        int order = -1;
        if (node.has("order"))
            order = (Integer) ((IntNode) node.get("order")).numberValue();

        boolean tail = false;
        if (node.has("tail"))
            tail = ((BooleanNode) node.get("tail")).booleanValue();

        switch (content) {
            case MapObstacle.CONTENT  : return new MapObstacle();
            case MapFood.CONTENT      : return new MapFood();
            case MapSnakeHead.CONTENT : return new MapSnakeHead(name, playerId);
            case MapSnakeBody.CONTENT : return new MapSnakeBody(tail, playerId, order);
            default: return new MapEmpty();
        }
    }
}