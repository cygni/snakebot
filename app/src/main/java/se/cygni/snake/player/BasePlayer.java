package se.cygni.snake.player;

import se.cygni.snake.api.model.PointReason;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BasePlayer implements IPlayer {

    private Map<PointReason, AtomicInteger> pointsDistribution;
    private boolean alive = true;
    private boolean connected = true;
    private boolean inTournament = true;
    private int accumulatedPoints = 0;
    private long diedAtGameTick = -1;

    public BasePlayer() {
        pointsDistribution = new HashMap<>();
    }

    @Override
    public int compareTo(IPlayer o2) {
        return new IPlayerComparator().compare(this, o2);
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
    public void dead(long gameTick) {
        alive = false;
        diedAtGameTick = gameTick;
    }

    @Override
    public void revive() {
        alive = true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isInTournament() {
        return inTournament;
    }

    @Override
    public void outOfTournament() {
        inTournament = false;
    }

    @Override
    public void lostConnection(long gameTick) {
        dead(gameTick);
        connected = false;
    }

    @Override
    public void addPoints(PointReason reason, int points) {
        accumulatedPoints += points;

        if (!pointsDistribution.containsKey(reason)) {
            pointsDistribution.put(reason, new AtomicInteger(0));
        }
        pointsDistribution.get(reason).addAndGet(points);
    }

    @Override
    public int getPointsBy(PointReason reason) {
        if (pointsDistribution.containsKey(reason)) {
            return pointsDistribution.get(reason).get();
        }
        return 0;
    }

    @Override
    public void reset() {
        pointsDistribution.clear();
        accumulatedPoints = 0;
        diedAtGameTick = -1;
    }

    @Override
    public int getTotalPoints() {
        return accumulatedPoints;
    }

}
