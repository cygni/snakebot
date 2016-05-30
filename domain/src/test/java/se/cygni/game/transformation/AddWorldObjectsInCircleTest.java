package se.cygni.game.transformation;

import org.junit.Assert;
import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.WorldObject;

import java.util.ArrayList;

import java.util.Collection;


public class AddWorldObjectsInCircleTest {

    static int stuffCounter = 0;
    private static Collection<WorldObject> stuff(int number){
        Collection<WorldObject> ret = new ArrayList<>();
        for(int i = 0; i < number; ++i){
            ret.add(new SnakeHead(stuffCounter+"troll"+i, stuffCounter+"TROLL"+ i,0));
        }
        assert ret.size() == number;
        stuffCounter++;
        return ret;
    }

    @Test
    public void crowdedWorld(){
        AddWorldObjectsInCircle first = new AddWorldObjectsInCircle(stuff(10), 0.9d);
        WorldState worldState = first.transform(new WorldState(10, 2));
        AddWorldObjectsInCircle second = new AddWorldObjectsInCircle(stuff(10), 0.9d);
        worldState = second.transform(worldState);
        int[] ints = worldState.listEmptyPositions();
        Assert.assertEquals("world should be full", 0, ints.length);
    }

}