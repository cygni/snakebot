package se.cygni.snake.apiconversion;

import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
import se.cygni.game.worldobject.*;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.model.*;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

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

        Map map = WorldStateConverter.convertWorldState(ws, 1);

        // Make sure serialisation works
        String mapStr = GameMessageParser.encodeMessage(map);

        // Make sure deserialisation works
        Map reparsedMap = (Map) GameMessageParser.decodeMessage(mapStr);
//        System.out.println(reparsedMap);

        SnakeInfo sn1 = map.getSnakeInfos()[0];
        SnakeInfo sn2 = map.getSnakeInfos()[1];

//        System.out.println(sn1.getName() + ", id: " + sn1.getId() + ", length: " + sn1.getLength());
//        System.out.println(sn2.getName() + ", id: " + sn2.getId() + ", length: " + sn2.getLength());

//        System.out.println("\n");
//        System.out.println(mapStr);
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

        WorldStateConverter converter = new WorldStateConverter();
        Map map = converter.convertWorldState(ws, 1);

        // Make sure serialisation works
        String mapStr = GameMessageParser.encodeMessage(map);

        // Make sure deserialisation works
        Map reparsedMap = (Map) GameMessageParser.decodeMessage(mapStr);

        // Assert snakeinfo
        assertEquals(1, reparsedMap.getSnakeInfos().length);
        SnakeInfo si = reparsedMap.getSnakeInfos()[0];
        assertEquals(3, si.getLength());
        assertEquals("junit", si.getName());
        assertEquals("id", si.getId());
        assertEquals(2, si.getX());
        assertEquals(1, si.getY());

        // Assert snake position
        assertEquals(MapSnakeHead.class, reparsedMap.getTiles()[2][1].getClass());
        assertEquals(MapSnakeBody.class, reparsedMap.getTiles()[1][1].getClass());
        assertEquals(MapSnakeBody.class, reparsedMap.getTiles()[0][1].getClass());

        // Assert snake values
        MapSnakeHead mapSnakeHead = (MapSnakeHead)reparsedMap.getTiles()[2][1];
        assertEquals(snakeName, mapSnakeHead.getName());
        assertEquals("id", mapSnakeHead.getPlayerId());

        MapSnakeBody mapSnakeBody1 = (MapSnakeBody)reparsedMap.getTiles()[1][1];
        assertEquals("id", mapSnakeBody1.getPlayerId());
        assertEquals(1, mapSnakeBody1.getOrder());
        assertFalse(mapSnakeBody1.isTail());

        MapSnakeBody mapSnakeBody2 = (MapSnakeBody)reparsedMap.getTiles()[0][1];
        assertEquals("id", mapSnakeBody2.getPlayerId());
        assertEquals(2, mapSnakeBody2.getOrder());
        assertTrue(mapSnakeBody2.isTail());

    }

    private <T extends WorldObject> void testConversionWithType(Class<T> clazz) throws Exception {
        WorldState ws = new WorldState(3, 4);

        WorldObject worldObject = SnakeTestUtil.createWorldObject(clazz);

        // Obstacle at 1,1
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, worldObject, 4);

        WorldStateConverter converter = new WorldStateConverter();
        Map map = converter.convertWorldState(ws, 1);

        // Make sure serialisation works
        String mapStr = GameMessageParser.encodeMessage(map);

        // Make sure deserialisation works
        Map reparsedMap = (Map) GameMessageParser.decodeMessage(mapStr);

        // Assert values
        assertEquals(3, reparsedMap.getWidth());
        assertEquals(4, reparsedMap.getHeight());

        Class mapType = getCorrespondingMapType(worldObject);

        IntStream.range(0, 3).forEach(x -> {
            IntStream.range(0, 4).forEach(y -> {
                if (x == 1 && y == 1) {
                    assertEquals(mapType, reparsedMap.getTiles()[x][y].getClass());
                } else {
                    assertEquals(MapEmpty.class, reparsedMap.getTiles()[x][y].getClass());
                }
            });
        });

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

    @Test @Ignore
    public void testStreams() throws Exception {
        int [] result = IntStream.concat(
                IntStream.range(0, 10),
                IntStream.range(0, 10).flatMap(x -> IntStream.of(generate(x))))
                .toArray();

        assertEquals(40, result.length);
    }

    private int[] generate(int x) {
        return new int[] {66,66,66};
    }
}