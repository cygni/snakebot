package se.cygni.snake.eventapi.model;

import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

import java.util.List;

@ApiMessageType
public class TournamentGamePlan extends ApiMessage {

    private int noofLevels;
    private List<ActiveGamePlayer> players;

    private String tournamentName;
    private String tournamentId;

    private List<TournamentLevel> tournamentLevels;

    public TournamentGamePlan(int noofLevels, String tournamentName, String tournamentId) {
        this.noofLevels = noofLevels;
        this.tournamentName = tournamentName;
        this.tournamentId = tournamentId;
    }

    public int getNoofLevels() {
        return noofLevels;
    }

    public void setNoofLevels(int noofLevels) {
        this.noofLevels = noofLevels;
    }

    public List<ActiveGamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ActiveGamePlayer> players) {
        this.players = players;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public List<TournamentLevel> getTournamentLevels() {
        return tournamentLevels;
    }

    public void setTournamentLevels(List<TournamentLevel> tournamentLevels) {
        this.tournamentLevels = tournamentLevels;
    }
}
