package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MapFood implements TileContent {

    public static final String CONTENT = "food";

    @Override
    public String getContent() {
        return CONTENT;
    }

    @JsonIgnore
    public String toDisplay() {
        return "F";
    }
}
