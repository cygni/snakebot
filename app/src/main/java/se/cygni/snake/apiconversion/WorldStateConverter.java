package se.cygni.snake.apiconversion;

import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.player.IPlayer;

import java.util.Set;

public class WorldStateConverter {

    public static Map convertWorldState(WorldState ws, long worldTick, Set<IPlayer> players) {

        int width = ws.getWidth();
        int height = ws.getHeight();

        SnakeInfo[] snakeInfos = getSnakeInfos(ws, players);
        int[] foods = ws.listFoodPositions();
        int[] obstacles = ws.listObstaclePositions();

        return new Map(
                width,
                height,
                worldTick,
                snakeInfos,
                foods,
                obstacles);
    }

    private static SnakeInfo[] getSnakeInfos(WorldState ws, Set<IPlayer> players) {

        SnakeInfo[] snakeInfos = new SnakeInfo[players.size()];

        int c = 0;
        for (IPlayer player : players) {
            snakeInfos[c++] = getSnakeInfo(ws, player);
        }

        return snakeInfos;
    }

    private static SnakeInfo getSnakeInfo(WorldState ws, IPlayer player) {
        String name = player.getName();
        String id = player.getPlayerId();
        int points = player.getTotalPoints();

        try {
            SnakeHead snakeHead = ws.getSnakeHeadById(id);
            return getSnakeInfo(ws, snakeHead);
        } catch (Exception e) {}

        return new SnakeInfo(name, points, id, new int[] {}, 0);
    }

    private static SnakeInfo getSnakeInfo(WorldState ws, SnakeHead head) {
        String name = head.getName();
        String id = head.getPlayerId();

        int[] positions = ws.getSnakeSpread(head);

        return new SnakeInfo(name, head.getPoints(), id, positions, head.getTailProtectedForGameTicks());
    }

}
