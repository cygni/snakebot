package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.apiconversion.DirectionConverter;
import se.cygni.snake.player.IPlayer;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomBot extends BotPlayer {

    public RandomBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }

    @Override
    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick, Set<IPlayer> players) {

        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            postNextMove(worldState, gameId, gameTick);
        });
    }

    private void postNextMove(WorldState worldState, String gameId, long gameTick) {

        int myPos = getMyPosition(worldState);
        if (myPos < 0)
            throw new RuntimeException("Got negative position...");

        SnakeDirection rndDirection = getRandomDirection();
        List<SnakeDirection> validDirections = getValidDirections(worldState, getMyPosition(worldState));
        if (validDirections.size() > 0)
            rndDirection = getRandomDirection(validDirections);

        RegisterMove registerMove = new RegisterMove(gameTick, rndDirection);
        registerMove.setReceivingPlayerId(playerId);
        incomingEventbus.post(
                registerMove);
    }

    private List<SnakeDirection> getValidDirections(WorldState ws, int myPos) {

        // Find all adjacent tiles (that are valid) and that are empty or contains food.
        // Then map them to SnakeDirections.
        List<SnakeDirection> validDirections = Stream.of(Direction.values()).filter(direction ->
            ws.hasAdjacentTile(myPos, direction) && (
                    ws.isTileEmpty(
                            ws.getPositionForAdjacent(myPos, direction)) ||
                            ws.isTileContentOfType(ws.getPositionForAdjacent(myPos, direction), Food.class))
        ).map(direction1 -> DirectionConverter.toSnakeDirection(direction1)).collect(Collectors.toList());

        return validDirections;
    }

    private int getMyPosition(WorldState ws) {
        int[] snakeHeads = ws.listPositionsWithContentOf(SnakeHead.class);
        for (int pos : snakeHeads) {
            if (((SnakeHead)ws.getTile(pos).getContent()).getPlayerId().equals(getPlayerId()))
                return pos;
        }
        return -1;
    }

    private SnakeDirection getRandomDirection(List<SnakeDirection> directions) {
        int max = directions.size()-1;
        if (max == 0)
            return directions.get(0);

        Random r = new Random();
        return directions.get(r.nextInt(max));
    }

    private SnakeDirection getRandomDirection() {
        int max = SnakeDirection.values().length-1;

        Random r = new Random();
        return SnakeDirection.values()[r.nextInt(max)];
    }

    @Override
    public void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {
        super.onPlayerDied(reason, playerId, x, y, gameId, gameTick);
    }

    @Override
    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState, Set<IPlayer> players) {
        super.onGameEnded(playerWinnerId, gameId, gameTick, worldState, players);
    }

    @Override
    public void onGameStart(String gameId, int noofPlayers, int width, int height) {
    }
}
