package se.cygni.game.testutil;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
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
            return clazz.newInstance();
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
}
