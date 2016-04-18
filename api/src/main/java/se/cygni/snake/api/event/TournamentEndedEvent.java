package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.PlayerPoints;
import se.cygni.snake.api.type.GameMessageType;

import java.util.List;

@GameMessageType
public class TournamentEndedEvent extends GameMessage {

    private final String playerWinnerId;
    private final String gameId;
    private final List<PlayerPoints> gameResult;
    private final String tournamentName;
    private final String tournamentId;

    @JsonCreator
    public TournamentEndedEvent(
            @JsonProperty("playerWinnerId") String playerWinnerId,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameResult") List<PlayerPoints> gameResult,
            @JsonProperty("tournamentName") String tournamentName,
            @JsonProperty("tournamentId") String tournamentId) {

        this.playerWinnerId = playerWinnerId;
        this.gameId = gameId;
        this.gameResult = gameResult;
        this.tournamentName = tournamentName;
        this.tournamentId = tournamentId;
    }

    public TournamentEndedEvent(TournamentEndedEvent tee) {
        this.playerWinnerId = tee.getPlayerWinnerId();
        this.gameId = tee.getGameId();
        this.gameResult = tee.getGameResult();
        this.tournamentName = tee.getTournamentName();
        this.tournamentId = tee.getTournamentId();
    }

    public String getPlayerWinnerId() {
        return playerWinnerId;
    }

    public String getGameId() {
        return gameId;
    }

    public List<PlayerPoints> getGameResult() {
        return gameResult;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public String getTournamentId() {
        return tournamentId;
    }
}
