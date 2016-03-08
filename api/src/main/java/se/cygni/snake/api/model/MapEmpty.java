package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MapEmpty implements TileContent {

    public static final String CONTENT = "empty";

    @Override
    public String getContent() {
        return CONTENT;
    }

    @JsonIgnore
    public String toDisplay() {
        return " ";
    }
}
