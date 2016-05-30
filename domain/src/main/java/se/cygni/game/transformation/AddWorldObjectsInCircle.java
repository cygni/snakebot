package se.cygni.game.transformation;

import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

import java.util.Collection;

/**
 * Adds a set of WorldObject randomly in a centerd circle formation on empty cells. If cells are taken next cell index is used.
 */
public class AddWorldObjectsInCircle implements WorldTransformation {

    private final Collection<? extends WorldObject> worldObjects;
    private final double scaleFactor;


    public AddWorldObjectsInCircle(Collection<? extends WorldObject> worldObjects, double scaleFactor) {
        this.worldObjects = worldObjects;
        this.scaleFactor = scaleFactor;
    }

    public static class NoRoomInWorldException extends RuntimeException {

    }

    @Override
    public WorldState transform(WorldState currentWorld) {

        if (worldObjects.isEmpty()) {
            return currentWorld;
        }
        int[] emptyPositions = currentWorld.listEmptyPositions();
        if (emptyPositions.length < worldObjects.size()) {
            throw new NoRoomInWorldException();
        }

        int width = currentWorld.getWidth();
        int height = currentWorld.getHeight();

        double rotation = (2 * Math.PI) / worldObjects.size();

        double centerWidth = ((double) width) / 2;
        double centerHeight = ((double) height) / 2;

        double rotated = 0;
        for (WorldObject wo : worldObjects) {

            double sin = Math.sin(rotated);
            double cos = Math.cos(rotated);
            int widthScaled = (int) Math.floor(centerWidth + (sin * width * scaleFactor/2));
            int heightScaled = (int) Math.floor(centerHeight + (cos * height * scaleFactor/2));

            int tileNo = currentWorld.translateCoordinate(new Coordinate(widthScaled, heightScaled));
            currentWorld = setNextFreeTile(currentWorld, wo, tileNo);

            rotated += rotation;
        }

        return currentWorld;
    }

    static WorldState setNextFreeTile(WorldState ws, WorldObject worldObject, int index){
        boolean tileEmpty = ws.isTileEmpty(index);

        int width = ws.getWidth();
        int height = ws.getHeight();
        if (tileEmpty){
            if (worldObject instanceof SnakePart) {
                SnakePart snakePart = (SnakePart) worldObject;
                snakePart.setPosition(index);
            }
            Tile[] tiles = ws.getTiles();
            tiles[index] = new Tile(worldObject);
            return new WorldState(width, height, tiles);
        } else {
            return setNextFreeTile(ws, worldObject, index+1%(width * height));
        }
    }


}
