package se.cygni.game.transformation;

import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Adds WorldObject at random free Tiles on a grid, until the grid is full or the specified number has been added.
 */
public class AddWorldObjectAtRandomPositionInGrid {

    private final Random random;
    private final Supplier<WorldObject> worldObject;
    private final GridFilter gridFilter;


    public interface GridFilter {
        IntStream filterPositions(IntStream positions, int width, int height);
        GridFilter invert();
    }

    public AddWorldObjectAtRandomPositionInGrid(Random random, Supplier<WorldObject> worldObject, GridFilter gridFilter) {
        this.random = random;
        this.worldObject = worldObject;
        this.gridFilter = gridFilter;
    }

    public static GridFilter fromStringMap(String map, int mapWidth) {
        int mapHeight = map.length() / mapWidth;
        return filter(map, mapWidth, mapHeight, ' ', '#');
    }

    private static GridFilter filter(final String map, final int mapWidth, final int mapHeight, final char testChar, char nextTest) {
        return new GridFilter() {
            @Override
            public IntStream filterPositions(IntStream positions, int width, int height) {

                return positions.filter(value -> {
                    int x = value / width;
                    int y = value % height;
                    int mapX = (int) Math.floor(((double) x) / width * mapWidth);
                    int mapY = (int) Math.floor(((double) y) / height * mapHeight);
                    return map.charAt(mapX * mapWidth + mapY) == testChar;
                });
            }

            @Override
            public GridFilter invert() {
                return filter(map, mapWidth, mapHeight, nextTest, testChar);
            }
        };
    }

    public WorldState gridInsert(WorldState currentWorld, int count) {

        int[] emptyPositions = currentWorld.listEmptyPositions();
        if (emptyPositions.length == 0) {
            return currentWorld;
        }

        Set<Integer> free = Arrays.stream(emptyPositions).boxed().collect(Collectors.toSet());

        Set<Integer> closeToHeads = Arrays.stream(currentWorld.listPositionsAdjacentToSnakeHeads())
                .flatMap(value -> IntStream.concat(
                        IntStream.of(value),
                        IntStream.of(currentWorld.listAdjacentTiles(value))))
                .boxed().collect(Collectors.toSet());

        IntStream intStream = free.stream().filter(o -> !closeToHeads.contains(o)).mapToInt(i -> i);
        IntStream onGrid = gridFilter.filterPositions(intStream, currentWorld.getWidth(), currentWorld.getHeight());
        List<Integer> possiblePositions = onGrid.boxed().collect(Collectors.toList());
        Collections.shuffle(possiblePositions, random);
        List<Integer> insert = keepFirst(possiblePositions, count);

        Tile[] tiles = currentWorld.getTiles();
        for (Integer tileNo : insert) {
            WorldObject supplied = worldObject.get();
            tiles[tileNo] = insertableTile(tileNo, supplied);
        }

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }

    private static Tile insertableTile(Integer tileNo, WorldObject supplied) {
        if (supplied instanceof SnakePart) {
            SnakePart snakePart = (SnakePart) supplied;
            snakePart.setPosition(tileNo);
        }
        return new Tile(supplied);
    }

    static <T> List<T> keepFirst(List<T> items, int keepItems) {
        return new ArrayList<T>(items.subList(0, Math.min(items.size(),keepItems)));
    }

}
