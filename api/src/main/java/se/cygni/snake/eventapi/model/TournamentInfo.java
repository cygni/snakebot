package se.cygni.snake.eventapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessage;

/**
 * Created by mbark on 2016-09-28.
 */
public class TournamentInfo extends ApiMessage {
    private final String tournamentId;
    private final String tournamentName;
    private final GameSettings gameSettings;
    private final TournamentGamePlan gamePlan;

    @JsonCreator
    public TournamentInfo(
            @JsonProperty("tournamentId") String tournamentId,
            @JsonProperty("tournamentName") String tournamentName,
            @JsonProperty("gameSettings") GameSettings gameSettings,
            @JsonProperty("gamePlan") TournamentGamePlan gamePlan) {

        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.gameSettings = gameSettings;
        this.gamePlan = gamePlan;
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

    public TournamentGamePlan getGamePlan() {
        return gamePlan;
    }
}
