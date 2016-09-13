package se.cygni.snake.apiconversion;

import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
import se.cygni.game.worldobject.*;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.*;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.bot.RandomBot;

import java.util.HashSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WorldStateConverterTest {

    @Test
    public void testConvertWorldStateWithObstacle() throws Exception {
        testConversionWithType(Obstacle.class);
    }

    @Test
    public void testConvertWorldStateWithFood() throws Exception {
        testConversionWithType(Food.class);
    }

    @Test @Ignore
    public void testPrintCoordinatePosition() {
        int size = 15;
        int counter = 0;
        for (int x=0; x<size; x++) {
            for (int y=0; y<size; y++) {
                System.out.printf("%03d ", counter++);
            }
            System.out.println("\n");
        }
    }

    @Test
    public void testLargerMapToJson() throws Exception {
        WorldState ws = new WorldState(15, 15);

        // Snake Python
        String snakeName = "python";
        SnakeHead head = new SnakeHead(snakeName, "id_python", 101);
        SnakeBody body1 = new SnakeBody("id_python", 116);
        SnakeBody body2 = new SnakeBody("id_python", 115);

        head.setNextSnakePart(body1);
        body1.setNextSnakePart(body2);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, head.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body1, body1.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body2, body2.getPosition());

        // Snake Cobra
        String snakeName2 = "cobra";
        SnakeHead head2 = new SnakeHead(snakeName2, "id_cobra", 109);
        SnakeBody body21 = new SnakeBody("id_cobra", 108);
        SnakeBody body22 = new SnakeBody("id_cobra", 123);
        SnakeBody body23 = new SnakeBody("id_cobra", 138);

        head2.setNextSnakePart(body21);
        body21.setNextSnakePart(body22);
        body22.setNextSnakePart(body23);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head2, head2.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body21, body21.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body22, body22.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body23, body23.getPosition());

        // 10 Obstacles
        for (int x=0; x<10; x++) {
            AddWorldObjectAtRandomPosition ar = new AddWorldObjectAtRandomPosition(new Obstacle());
            ws = ar.transform(ws);
        }

        // 5 Foods
        for (int x=0; x<10; x++) {
            AddWorldObjectAtRandomPosition ar = new AddWorldObjectAtRandomPosition(new Food());
            ws = ar.transform(ws);
        }

        HashSet<IPlayer> players = new HashSet<>();
        players.add(new RandomBot("id_cobra", null));
        players.add(new RandomBot("id_python", null));

        Map map = WorldStateConverter.convertWorldState(ws, 1, players);

        // Make sure serialisation works
        MapUpdateEvent mue = new MapUpdateEvent(0, "id", map);
        String mapUpdateStr = GameMessageParser.encodeMessage(mue);

        // Make sure deserialisation works
        MapUpdateEvent mueReparsed = (MapUpdateEvent) GameMessageParser.decodeMessage(mapUpdateStr);
        Map reparsedMap = mueReparsed.getMap();

        SnakeInfo sn1 = map.getSnakeInfos()[0];
        SnakeInfo sn2 = map.getSnakeInfos()[1];

        assertArrayEquals(new int[] {101,116,115}, sn1.getPositions());
        assertArrayEquals(new int[] {109,108,123,138}, sn2.getPositions());

        assertEquals(10, reparsedMap.getFoodPositions().length);
        assertEquals(10, reparsedMap.getObstaclePositions().length);

    }

    @Test
    public void testConvertWorldStateWithOneSnake() throws Exception {
        WorldState ws = new WorldState(3, 4);

        String snakeName = "junit";
        SnakeHead head = new SnakeHead(snakeName, "id", 5);
        SnakeBody body1 = new SnakeBody("id", 4);
        SnakeBody body2 = new SnakeBody("id", 3);

        head.setNextSnakePart(body1);
        body1.setNextSnakePart(body2);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, 5);  // 5 => (2,1)
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body1, 4); // 4 => (1,1)
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body2, 3); // 3 => (0,1)

        HashSet<IPlayer> players = new HashSet<>();
        players.add(new RandomBot("id", null));

        WorldStateConverter converter = new WorldStateConverter();
        Map map = converter.convertWorldState(ws, 1, players);

        // Make sure serialisation works
        MapUpdateEvent mue = new MapUpdateEvent(0, "id", map);
        String mapUpdateStr = GameMessageParser.encodeMessage(mue);


        // Make sure deserialisation works
        MapUpdateEvent mueReparsed = (MapUpdateEvent) GameMessageParser.decodeMessage(mapUpdateStr);
        Map reparsedMap = mueReparsed.getMap();

        // Assert snakeinfo
        assertEquals(1, reparsedMap.getSnakeInfos().length);
        SnakeInfo si = reparsedMap.getSnakeInfos()[0];
        assertEquals(3, si.getLength());
        assertEquals("junit", si.getName());
        assertEquals("id", si.getId());
        assertArrayEquals(new int[]{5,4,3}, si.getPositions());


    }

    private <T extends WorldObject> void testConversionWithType(Class<T> clazz) throws Exception {
        WorldState ws = new WorldState(3, 4);

        WorldObject worldObject = SnakeTestUtil.createWorldObject(clazz);

        // Obstacle at 1,1
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, worldObject, 4);

        WorldStateConverter converter = new WorldStateConverter();
        Map map = converter.convertWorldState(ws, 1, new HashSet<>());

        MapUpdateEvent mue = new MapUpdateEvent(0, "id", map);

        // Make sure serialisation works
        String mapUpdateStr = GameMessageParser.encodeMessage(mue);

        // Make sure deserialisation works
        MapUpdateEvent mueReparsed = (MapUpdateEvent) GameMessageParser.decodeMessage(mapUpdateStr);
        Map reparsedMap = mueReparsed.getMap();

        // Assert values
        assertEquals(3, reparsedMap.getWidth());
        assertEquals(4, reparsedMap.getHeight());

        Class mapType = getCorrespondingMapType(worldObject);

        if (clazz == Obstacle.class) {
            assertArrayEquals(new int[] {4}, reparsedMap.getObstaclePositions());
        } else {
            assertArrayEquals(new int[] {4}, reparsedMap.getFoodPositions());

        }

        // No snakeinfo
        assertEquals(0, map.getSnakeInfos().length);
    }

    private Class getCorrespondingMapType(WorldObject obj) {
        if (obj instanceof Obstacle)
            return MapObstacle.class;

        if (obj instanceof Food)
            return MapFood.class;

        if (obj instanceof Empty)
            return MapEmpty.class;

        throw new IllegalArgumentException(obj.getClass() + " is not a known type");
    }

}