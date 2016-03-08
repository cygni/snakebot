package se.cygni.snake.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import se.cygni.snake.api.deserializer.TileContentDeserializer;

@JsonDeserialize(using = TileContentDeserializer.class)
public interface TileContent {
    String getContent();
    String toDisplay();
}
