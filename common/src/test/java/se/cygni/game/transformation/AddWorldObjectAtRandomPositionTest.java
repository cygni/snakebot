package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class AddWorldObjectAtRandomPositionTest {

    @Test
    public void testAddRandomWorldObjectTransform() throws Exception {
        WorldState world = new WorldState(3,3);

        Food food = new Food();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(food);
        WorldState transformedWorld = randomFood.transform(world);

        int noofTilesNotEmpty = 0;
        int noofTilesFood = 0;

        for (Tile tile : transformedWorld.getTiles()) {
            if (!(tile.getContent() instanceof Empty)) {
                noofTilesNotEmpty++;
            }
            if (tile.getContent() instanceof Food) {
                noofTilesFood++;
                assertEquals(food, tile.getContent());
            }
        }

        assertEquals(1, noofTilesNotEmpty);
        assertEquals(1, noofTilesFood);
    }

    @Test
    public void testAddRandomSeveralWorldObjectsTransform() throws Exception {
        WorldState world = new WorldState(3,3);

        Obstacle obstacle = new Obstacle();
        AddWorldObjectAtRandomPosition randomObstacle = new AddWorldObjectAtRandomPosition(obstacle);
        WorldState transformedWorld = randomObstacle.transform(world);

        Food food = new Food();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(food);
        transformedWorld = randomFood.transform(transformedWorld);

        int noofTilesNotEmpty = 0;
        int noofTilesFood = 0;
        int noofTilesObstacle = 0;

        for (Tile tile : transformedWorld.getTiles()) {
            if (!(tile.getContent() instanceof Empty)) {
                noofTilesNotEmpty++;
            }
            if (tile.getContent() instanceof Food) {
                noofTilesFood++;
                assertEquals(food, tile.getContent());
            }
            if (tile.getContent() instanceof Obstacle) {
                noofTilesObstacle++;
                assertEquals(obstacle, tile.getContent());
            }
        }

        assertEquals(2, noofTilesNotEmpty);
        assertEquals(1, noofTilesFood);
        assertEquals(1, noofTilesObstacle);
    }

    @Test
    public void testSameWordStateIsReturnedIfAlreadyFull() throws Exception {

        // Create world full of Obstacles
        WorldState ws = SnakeTestUtil.createWorld(Obstacle.class, 10, 10, IntStream.range(0,100).toArray());

        Food food = new Food();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(food);

        ws = randomFood.transform(ws);

        assertEquals(100, ws.listObstaclePositions().length);
    }

    @Test
    public void testWithOnlyOneFreeTile() throws Exception {

        // Create world full of Obstacles
        WorldState ws = SnakeTestUtil.createWorld(Obstacle.class, 10, 10, IntStream.range(0,100).toArray());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, new Empty(), 50);

        Food food = new Food();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(food);

        ws = randomFood.transform(ws);

        assertEquals(99, ws.listObstaclePositions().length);
        assertEquals(1, ws.listFoodPositions().length);
    }
}