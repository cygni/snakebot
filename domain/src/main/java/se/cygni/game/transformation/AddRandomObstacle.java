package se.cygni.game.transformation;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.game.worldobject.Obstacle;

import java.util.Random;

public class AddRandomObstacle implements WorldTransformation {

    private Random random = new XORShiftRandom();
    final static int[] sizes = new int[] {1, 2, 3};
    final static int MAX_RETRIES = 15;

    @Override
    public WorldState transform(WorldState currentWorld) {

        int[] validPositions = currentWorld.listEmptyPositionsWithPadding();

        if (validPositions.length == 0) {
            return currentWorld;
        }

        Tile[] tiles = currentWorld.getTiles();

        int choosenSize = sizes[random.nextInt(3)];

        boolean placedObstacle = false;
        int noofRetries = 0;

        while (!placedObstacle && noofRetries < MAX_RETRIES) {
            noofRetries++;

            int randomPosition = validPositions[random.nextInt(validPositions.length)];

            int[] positionsNeeded = listPositions(choosenSize, randomPosition, currentWorld);

            if (isObstacleWithinBounds(randomPosition, choosenSize, currentWorld) &&
                    areAllPositionsAvailable(positionsNeeded, validPositions)) {

                tiles = placeObstacle(positionsNeeded, currentWorld);
                placedObstacle = true;
            }

            if (!placedObstacle)
                System.out.println("\n\n\nHad to replace obstacle!\n\n");
        }

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }

    private Tile[] placeObstacle(int[] positions, WorldState currentWorld) {

        Tile[] tiles = currentWorld.getTiles();

        for (int pos : positions) {
            tiles[pos] = new Tile(new Obstacle());
        }

        return tiles;
    }

    private int[] listPositions(int choosenSize, int position, WorldState worldState) {
        Coordinate sCoord = worldState.translatePosition(position);

        int[] positions = new int[choosenSize*choosenSize];

        int counter = 0;
        for (int x = 0; x < choosenSize; x++) {
            for (int y = 0; y < choosenSize; y++) {
                int pos = worldState.translateCoordinate(sCoord.translate(x, y));
                positions[counter++] = pos;
            }
        }

        return positions;
    }

    private boolean isObstacleWithinBounds(int pos, int size, WorldState worldState) {
        if (size == 1) {
            return true;
        }

        Coordinate coordinate = worldState.translatePosition(pos);
        return coordinate.getX() + size < worldState.getWidth() &&
                coordinate.getY() + size < worldState.getHeight();
    }

    private boolean areAllPositionsAvailable(int[] positions, int[] validPositions) {
        for (int pos : positions) {
            if (!ArrayUtils.contains(validPositions, pos)) {
                return false;
            }
        }

        return true;
    }
}
