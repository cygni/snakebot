package se.cygni.game.testutil;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

import java.util.Arrays;

public class SnakeTestUtil {

    public static WorldState createWorld(
            Class<? extends WorldObject> worldType,
            int width, int height, int...positions) throws Exception {

        WorldState ws = new WorldState(width, height);

        Tile[] tiles = ws.getTiles();

        // Add tiles of worldType
        Arrays.stream(positions).forEach(position-> {
            tiles[position] = new Tile(createWorldObject(worldType));
        });

        return new WorldState(width, height, tiles);
    }

    public static <T extends WorldObject> T createWorldObject(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of " + clazz.getSimpleName(), e);
        }
    }

    public static WorldState replaceWorldObjectAt(
            WorldState ws,
            WorldObject item,
            int position) {

        Tile[] tiles = ws.getTiles();
        tiles[position] = new Tile(item);
        return new WorldState(ws.getWidth(), ws.getHeight(), tiles);
    }

    public static WorldState addSnake(
            WorldState ws,
            SnakePart...snakeParts) {

        Tile[] tiles = ws.getTiles();
        for (SnakePart sp : snakeParts) {
            tiles[sp.getPosition()] = new Tile(sp);
        }
        return new WorldState(ws.getWidth(), ws.getHeight(), tiles);
    }

    /**
     * Creates a Snake with the head at the first position in the array
     * @param positions
     * @return An array of SnakePart
     */
    public static SnakePart[] createSnake(String name, String playerId, int...positions) {
        SnakePart[] parts = new SnakePart[positions.length];

        SnakePart previousSnakePart = null;
        for (int pos = positions.length-1; pos >= 0; pos--) {
            if (pos > 0) {
                previousSnakePart = new SnakeBody(playerId, previousSnakePart, positions[pos]);
            } else {
                SnakeHead sh = new SnakeHead(name, playerId, positions[pos]);
                sh.setNextSnakePart(previousSnakePart);
                previousSnakePart = sh;
            }

            parts[pos] = previousSnakePart;
        }

        return parts;
    }
}
