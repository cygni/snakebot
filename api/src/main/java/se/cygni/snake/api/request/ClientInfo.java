package se.cygni.snake.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
@GameMessageType
public class ClientInfo extends GameMessage {

    private final String language;
    private final String operatingSystem;
    private final String ipAddress;
    private final String clientVersion;

    @JsonCreator
    public ClientInfo(@JsonProperty("language") String language,
                      @JsonProperty("operatingSystem") String operatingSystem,
                      @JsonProperty("ipAddress") String ipAddress,
                      @JsonProperty("clientVersion") String clientVersion) {
        this.language = language;
        this.operatingSystem = operatingSystem;
        this.ipAddress = ipAddress;
        this.clientVersion = clientVersion;
    }

    public String getLanguage() {
        return language;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "language='" + language + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
