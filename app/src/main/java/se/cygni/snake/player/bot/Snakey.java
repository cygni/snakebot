package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.player.bot.snakey.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class Snakey extends BotPlayer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Snakey.class);

    //Final variables
    private SnakeState currentState;
    private static final int MAX_SEARCH_DEPTH = 30; //Play with this value for search depth


    private SnakeInfo[] snakes;
    private MapUtil mapUtil;
    private Map map;
    private MapUpdateEvent mue;
    private HashSet<MapCoordinate> foodSet;
    private int finalOpenSpaces;

    public Snakey(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {
        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            upDateInstance(mapUpdateEvent);

            //Needed for multiple games with same instance
            if (mapUpdateEvent.getGameTick() > 0) {
                updateState();
            } else {
                initiateState();
            }

            SnakeDirection bestDir = getBestPossibleDirection();

            if (foodSet.contains(getTileInFront(currentState.getSelf(), bestDir))) {
                currentState.getSelf().setHasEaten(true);
            }

            RegisterMove registerMove = new RegisterMove(mapUpdateEvent.getGameId(), mapUpdateEvent.getGameTick(), bestDir);
            registerMove.setReceivingPlayerId(playerId);
            incomingEventbus.post(registerMove);
        });
    }

    private void initiateState() {
        Snake self = null;
        ArrayList<Snake> foes = new ArrayList<>();
        foodSet = new HashSet<>();

        for (SnakeInfo snake : snakes) {
            MapCoordinate[] spread = mapUtil.getSnakeSpread(snake.getId());
            if (snake.getId().equals(getPlayerId())) {
                self = new Snake(getPlayerId(), spread, 0);
                self.setDir(SnakeDirection.DOWN);
            } else {
                Snake foe = new Snake(snake.getId(), spread, 0);
                foe.setDir(SnakeDirection.DOWN);
                foes.add(foe);
            }
        }

        currentState = new SnakeState(map.getHeight(), map.getWidth(), self, foes,
                mapUtil.listCoordinatesContainingObstacle());
    }

    private void updateState() {
        MapCoordinate newHead = mapUtil.getSnakeSpread(getPlayerId())[0];
        currentState.updateSnakeState(newHead, getFoeSnakes());
    }

    private HashSet<Snake> getFoeSnakes() {
        HashSet<Snake> foes = new HashSet<>();
        for (SnakeInfo snake : snakes) {
            if (!snake.getId().equals(getPlayerId()) && snake.isAlive()) {
                String fId = snake.getId();
                foes.add(new Snake(fId, mapUtil.getSnakeSpread(fId), mue.getGameTick()));
            }
        }
        return foes;
    }

    private void upDateInstance(MapUpdateEvent update) {
        mapUtil = new MapUtil(update.getMap(), getPlayerId());
        map = update.getMap();
        mue = update;
        snakes = map.getSnakeInfos();
        foodSet = new HashSet<>();

        for (MapCoordinate food : mapUtil.listCoordinatesContainingFood()) {
            foodSet.add(food);
        }
    }

    private SnakeDirection getBestPossibleDirection() {

        EnumMap<SnakeDirection, Tuple<Integer, Integer>> results = new EnumMap<>(SnakeDirection.class);
        EnumMap<SnakeDirection, Integer> collisionRisk = new EnumMap<>(SnakeDirection.class);
        BonusHandler bh = new BonusHandler();

        SnakeDirection bestDir = SnakeDirection.DOWN;
        int maxValue = Integer.MIN_VALUE;
        int maxTiles = 0;
        int mostFinalOpenSpaces = 0;

        for (SnakeDirection dir : SnakeDirection.values()) {
            finalOpenSpaces = 0;
            if (currentState.canIMoveInDirection(dir)) {
                BonusTracker bt = bh.addBonusTracker(dir);
                SnakeState futureState = currentState.createFutureState(dir);

                int searchVal = getLongestPossiblePath(futureState, bt, MAX_SEARCH_DEPTH);
                int openTiles = currentState.getOpenSpacesinDir(dir);

                collisionRisk.put(dir, getCollisionRisk(currentState, dir));

                if (searchVal > maxValue) {
                    maxValue = searchVal;
                    bestDir = dir;
                    maxTiles = openTiles;
                    mostFinalOpenSpaces = finalOpenSpaces;

                } else if (finalOpenSpaces > mostFinalOpenSpaces && openTiles > maxValue) {
                    bestDir = dir;
                    maxTiles = openTiles;
                    mostFinalOpenSpaces = finalOpenSpaces;
                } else if (openTiles > maxTiles) {
                    bestDir = dir;
                    maxTiles = openTiles;
                    mostFinalOpenSpaces = finalOpenSpaces;
                }

                results.put(dir, new Tuple<>(searchVal, openTiles));

            }

        }

        int maxBonus = bh.getBonus(bestDir);
        for (SnakeDirection dir : results.keySet()) {
            Tuple<Integer, Integer> resTuple = results.get(dir);
            if (dir != bestDir && resTuple.first >= maxValue && resTuple.second >= maxTiles && bh.getBonus(dir) > maxBonus) {
                bestDir = dir;
                maxBonus = bh.getBonus(dir);
            }
        }

        int leastRisk = collisionRisk.get(bestDir);
        if (leastRisk > 1) {
            for (SnakeDirection dir : collisionRisk.keySet()) {
                int colRisk = collisionRisk.get(dir);
                if (colRisk < leastRisk && (results.get(dir).first > maxValue * 0.6)) {
                    leastRisk = collisionRisk.get(dir);
                    bestDir = dir;

                }
            }
        }

        return bestDir;
    }

    private boolean isSelfMovingMid(SnakeState state) {
        MapCoordinate selfHead = state.getSelf().getHead();
        SnakeDirection selfDir = state.getSelf().getDir();
        int width = state.getMapWidth();
        int height = state.getMapHeight();

        if (selfHead.x < width / 2) {
            if (selfHead.y < height / 2) { //First and third quadrant
                return selfDir == SnakeDirection.RIGHT || selfDir == SnakeDirection.DOWN;
            } else
                return selfDir == SnakeDirection.RIGHT || selfDir == SnakeDirection.UP;
        } else {
            if (selfHead.y < height / 2) {
                return selfDir == SnakeDirection.LEFT || selfDir == SnakeDirection.DOWN;
            } else {
                return selfDir == SnakeDirection.LEFT || selfDir == SnakeDirection.UP;
            }
        }
    }

    private int finalizePath(SnakeState state) {
        if (state.canIMoveInDirection(state.getSelf().getDir())) {
            finalOpenSpaces = state.getOpenSpacesinDir(state.getSelf().getDir());
            return 0;
        } else {
            for (SnakeDirection dir : SnakeDirection.values()) {
                if (state.canIMoveInDirection(dir)) {
                    int spaces = state.getOpenSpacesinDir(dir);
                    if (spaces > finalOpenSpaces) {
                        finalOpenSpaces = spaces;
                    }
                }
            }
        }
        return 0;
    }

    private int getLongestPossiblePath(SnakeState state, BonusTracker bt, int depth) {
        if (depth <= 0) {
            return finalizePath(state);
        }

        checkBonusValue(state, bt, depth);

        SnakeDirection currentDir = state.getSelf().getDir();
        if (state.canIMoveInDirection(currentDir)) {
            return 1 + getLongestPossiblePath(state.createFutureState(currentDir), bt, depth - 1);
        } else {
            int mostOpenSpaces = 0;
            SnakeDirection bestDir = null;
            for (SnakeDirection dir : SnakeDirection.values()) {
                int openSpaces = state.getOpenSpacesinDir(dir);
                if (state.canIMoveInDirection(dir) && openSpaces > mostOpenSpaces) {
                    mostOpenSpaces = openSpaces;
                    bestDir = dir;

                }
            }

            if (bestDir != null) {
                return 1 + getLongestPossiblePath(state.createFutureState(bestDir), bt, depth - 1);
            }

            for (SnakeDirection dir : SnakeDirection.values()) {
                if (state.canIMoveInDirection(dir)) {
                    finalOpenSpaces = state.getOpenSpacesinDir(state.getSelf().getDir());
                }
            }
            return 0;
        }
    }

    private void checkBonusValue(SnakeState state, BonusTracker bt, int depth) {

        if (state.getFoes().size() > 1) {
            if (!isHeadWrapped(state) && depth >= MAX_SEARCH_DEPTH - 10) {
                bt.headFree();
            }

            if (depth >= MAX_SEARCH_DEPTH - 20 && foodSet.contains(state.getSelf().getHead())) {
                bt.foodFound(15);
            }

            if (depth >= MAX_SEARCH_DEPTH - 2 && isSelfMovingMid(state)) {
                bt.targetMiddle();
            }
        }


        if (depth >= MAX_SEARCH_DEPTH - 10 && state.getIsKilledFoeState()) {
            bt.killBonus();
        }
    }

    private int getCollisionRisk(SnakeState state, SnakeDirection dir) {
        int highRisk = 3 * getHighRiskValue(state, dir);
        int lowRisk = getLowRiskValue(state, dir);
        return highRisk + lowRisk;
    }

    //Both getLowriskValue and getHighRiskValue are ugly as sin. Oh well.
    private int getLowRiskValue(SnakeState state, SnakeDirection dir) {
        int lowRiskValue = 0;
        HashMap<MapCoordinate, SnakeDirection> riskPosition = new HashMap<>();
        MapCoordinate selfHead = state.getSelf().getHead();
        switch (dir) {
            case LEFT:
                riskPosition.put(selfHead.translateBy(-3, 0), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(-2, 1), null);
                riskPosition.put(selfHead.translateBy(-2, -1), null);
                riskPosition.put(selfHead.translateBy(-1, -2), SnakeDirection.UP);
                riskPosition.put(selfHead.translateBy(-1, 2), SnakeDirection.DOWN);
                break;
            case RIGHT:
                riskPosition.put(selfHead.translateBy(3, 0), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(2, 1), null);
                riskPosition.put(selfHead.translateBy(2, -1), null);
                riskPosition.put(selfHead.translateBy(1, -2), SnakeDirection.UP);
                riskPosition.put(selfHead.translateBy(1, 2), SnakeDirection.DOWN);
                break;
            case DOWN:
                riskPosition.put(selfHead.translateBy(0, 3), SnakeDirection.DOWN);
                riskPosition.put(selfHead.translateBy(-2, 1), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(2, 1), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(1, 2), null);
                riskPosition.put(selfHead.translateBy(-1, 2), null);
                break;
            case UP:
                riskPosition.put(selfHead.translateBy(0, -3), SnakeDirection.UP);
                riskPosition.put(selfHead.translateBy(-2, -1), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(2, -1), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(1, -2), null);
                riskPosition.put(selfHead.translateBy(-1, -2), null);
                break;

        }
        for (Snake foe : state.getFoes()) {
            MapCoordinate foeHead = foe.getHead();
            for (MapCoordinate riskPos : riskPosition.keySet()) {
                if (foeHead.equals(riskPos) && foe.getDir() != riskPosition.get(foeHead)) {
                    lowRiskValue++;
                }
            }
        }
        return lowRiskValue;
    }

    private int getHighRiskValue(SnakeState state, SnakeDirection dir) {
        int highRiskValue = 0;
        HashMap<MapCoordinate, SnakeDirection> riskPosition = new HashMap<>();
        MapCoordinate selfHead = state.getSelf().getHead();
        switch (dir) {
            case LEFT:
                riskPosition.put(selfHead.translateBy(-2, 0), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(-1, 1), SnakeDirection.DOWN);
                riskPosition.put(selfHead.translateBy(-1, -1), SnakeDirection.UP);
                break;
            case RIGHT:
                riskPosition.put(selfHead.translateBy(2, 0), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(1, 1), SnakeDirection.DOWN);
                riskPosition.put(selfHead.translateBy(1, -1), SnakeDirection.UP);
                break;
            case DOWN:
                riskPosition.put(selfHead.translateBy(-1, 1), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(1, 1), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(0, 2), SnakeDirection.DOWN);
                break;
            case UP:
                riskPosition.put(selfHead.translateBy(-1, -1), SnakeDirection.LEFT);
                riskPosition.put(selfHead.translateBy(1, -1), SnakeDirection.RIGHT);
                riskPosition.put(selfHead.translateBy(0, -2), SnakeDirection.UP);
                break;

        }
        for (Snake foe : state.getFoes()) {
            MapCoordinate foeHead = foe.getHead();
            for (MapCoordinate riskPos : riskPosition.keySet()) {
                if (foeHead.equals(riskPos) && foe.getDir() != riskPosition.get(foeHead)) {
                    highRiskValue++;
                }
            }
        }
        return highRiskValue;
    }

    private boolean isHeadWrapped(SnakeState state) {
        Snake self = state.getSelf();
        MapCoordinate selfHead = self.getHead();
        SnakeDirection selfDir = self.getDir();
        HashSet<MapCoordinate> selfBody = self.getBodySet();
        HashSet<MapCoordinate> blockades = state.getTotalSet();
        blockades.removeAll(selfBody);

        if (selfDir == SnakeDirection.DOWN || selfDir == SnakeDirection.UP) {
            return (blockades.contains(selfHead.translateBy(-1, 0))) ||
                    blockades.contains(selfHead.translateBy(1, 0)) ||
                    selfHead.x == 45 || selfHead.x == 0;
        } else {
            return (blockades.contains(selfHead.translateBy(0, 1))) ||
                    blockades.contains(selfHead.translateBy(0, -1)) ||
                    selfHead.y == 33 || selfHead.y == 0;
        }
    }


    private MapCoordinate getTileInFront(Snake snake, SnakeDirection dir) {
        switch (dir) {
            case RIGHT:
                return snake.getHead().translateBy(1, 0);
            case LEFT:
                return snake.getHead().translateBy(-1, 0);
            case DOWN:
                return snake.getHead().translateBy(0, 1);
            case UP:
                return snake.getHead().translateBy(0, -1);
            default:
                return snake.getHead();
        }

    }
}
