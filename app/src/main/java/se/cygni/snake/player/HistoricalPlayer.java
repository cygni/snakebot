package se.cygni.snake.player;

import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.PointReason;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// A representation of a players and its points in a previous game
public class HistoricalPlayer implements IPlayer {

    private final String name, playerId;

    private Map<PointReason, AtomicInteger> pointsDistribution;
    private boolean alive = true;
    private boolean connected = true;
    private boolean inTournament = true;
    private int accumulatedPoints = 0;
    private long diedAtGameTick = -1;
    private boolean isWinner;
    private boolean isMovedUpInTournament;

    public HistoricalPlayer(IPlayer player) {
        this.pointsDistribution = new HashMap<>();
        this.name = player.getName();
        this.playerId = player.getPlayerId();
        this.alive = player.isAlive();
        this.connected = player.isConnected();
        this.inTournament = player.isInTournament();
        this.accumulatedPoints = player.getTotalPoints();
        this.diedAtGameTick = player.getDiedAtTick();

        for (PointReason reason : PointReason.values()) {
            pointsDistribution.put(reason, new AtomicInteger(player.getPointsBy(reason)));
        }

    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public boolean isMovedUpInTournament() {
        return isMovedUpInTournament;
    }

    public void setMovedUpInTournament(boolean movedUpInTournament) {
        isMovedUpInTournament = movedUpInTournament;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public int getTotalPoints() {
        return accumulatedPoints;
    }

    @Override
    public int getPointsBy(PointReason reason) {
        if (pointsDistribution.containsKey(reason)) {
            return pointsDistribution.get(reason).get();
        }
        return 0;
    }

    @Override
    public int compareTo(IPlayer o) {
        return new IPlayerComparator().compare(this, o);
    }

    @Override
    public void lostConnection(long gameTick) {
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public long getDiedAtTick() {
        return diedAtGameTick;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isInTournament() {
        return false;
    }

    @Override
    public void outOfTournament() {
    }

    @Override
    public void dead(long gameTick) {
    }

    @Override
    public void revive() {
    }

    @Override
    public void addPoints(PointReason reason, int points) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {
    }

    @Override
    public void onGameResult(GameResultEvent gameResultEvent) {

    }

    @Override
    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent) {
    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
    }

    @Override
    public void onGameStart(GameStartingEvent gameStartingEvent) {
    }

    @Override
    public void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoricalPlayer that = (HistoricalPlayer) o;

        return playerId != null ? playerId.equals(that.playerId) : that.playerId == null;
    }

    @Override
    public int hashCode() {
        return playerId != null ? playerId.hashCode() : 0;
    }
}
