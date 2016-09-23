package se.cygni.snake.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

import java.util.UUID;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
@GameMessageType
public class ClientInfo extends GameMessage {

    private final String id = UUID.randomUUID().toString();

    private final String language;
    private final String languageVersion;
    private final String operatingSystem;
    private final String operatingSystemVersion;
    private final String clientVersion;

    @JsonCreator
    public ClientInfo(@JsonProperty("language") String language,
                      @JsonProperty("languageVersion") String languageVersion,
                      @JsonProperty("operatingSystem") String operatingSystem,
                      @JsonProperty("operatingSystemVersion") String operatingSystemVersion,
                      @JsonProperty("clientVersion") String clientVersion) {
        this.language = language;
        this.languageVersion = languageVersion;
        this.operatingSystem = operatingSystem;
        this.operatingSystemVersion = operatingSystemVersion;
        this.clientVersion = clientVersion;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "id='" + id + '\'' +
                ", language='" + language + '\'' +
                ", languageVersion='" + languageVersion + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", operatingSystemVersion='" + operatingSystemVersion + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
