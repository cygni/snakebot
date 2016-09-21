package se.cygni.snake.eventapi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
public abstract class ApiMessage {

    private final String type = this.getClass().getCanonicalName();

    public String getType() {
        return type;
    }

}
