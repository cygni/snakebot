package se.cygni.snake.eventapi.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class TournamentCreated extends ApiMessage {

    private final String tournamentId;
    private final String tournamentName;
    private final GameSettings gameSettings;

    @JsonCreator
    public TournamentCreated(
            @JsonProperty("tournamentId") String tournamentId,
            @JsonProperty("tournamentName") String tournamentName,
            @JsonProperty("gameSettings") GameSettings gameSettings) {

        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.gameSettings = gameSettings;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
