package se.cygni.game.transformation;

import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        int[] emptyArray = currentWorld.listEmptyPositions();


        if (emptyArray.length < worldObjects.size()) {
            throw new NoRoomInWorldException();
        }

        List<Integer> emptyPositions = IntStream.of(emptyArray).boxed().collect(Collectors.toList());
        Tile[] tiles = currentWorld.getTiles();

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
            Integer tileNo = currentWorld.translateCoordinate(new Coordinate(widthScaled, heightScaled));

            while(!emptyPositions.contains(tileNo)){
                tileNo = (tileNo+1)%(width * height);
            }
            emptyPositions.remove(tileNo);
            if (wo instanceof SnakePart) {
                SnakePart snakePart = (SnakePart) wo;
                snakePart.setPosition(tileNo);
            }
            tiles[tileNo] = new Tile(wo);
            rotated += rotation;
        }
        return new WorldState(width, height, tiles);
    }



}
