package se.cygni.snake.player.bot.snakey;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by trivo on 2017-03-28.
 */
public class Snake {

    private final String id;
    private SnakeDirection dir;
    private LinkedList<MapCoordinate> snakeSpread;
    private HashSet<MapCoordinate> bodySet;
    private long lifeTime;
    private boolean hasEaten;
    

    public Snake(Snake snake){
        this(snake.id, snake.getSnakeSpread().toArray
                (new MapCoordinate[snake.getSnakeSpread().size()]), snake.getLifeTime());

        this.hasEaten = false;
    }



    
    public Snake(String id, MapCoordinate[] spread, long lifeTime){
        this.id = id;
        snakeSpread = new LinkedList<>();
        bodySet = new HashSet<>();
        
        if(spread.length >= 2){
            dir = inferDir(spread[0], spread[1]);
        } else {
            dir = SnakeDirection.DOWN; //Not known yet
        }

        for(MapCoordinate coord : spread){
            snakeSpread.add(coord);
            bodySet.add(coord);
        }

        this.lifeTime = lifeTime;
        this.hasEaten = false;
    }

    private SnakeDirection inferDir(MapCoordinate newHead, MapCoordinate oldHead) {
        if(newHead.x < oldHead.x){
            return SnakeDirection.LEFT;
        } else if (newHead.x > oldHead.x){
            return SnakeDirection.RIGHT;
        } else if (newHead.y < oldHead.y){
            return SnakeDirection.UP;
        } else if (newHead.y > oldHead.y){
            return SnakeDirection.DOWN;
        }
        return SnakeDirection.DOWN;
    }




    public void updatePos(MapCoordinate newHead){
        MapCoordinate oldHead = snakeSpread.getFirst();
        snakeSpread.addFirst(newHead);
        bodySet.add(newHead);
        dir = inferDir(newHead, oldHead);
        increaseLife();
    }


    public void stepOnePos(){
        updatePos(getNewPos(dir));
    }

    private void increaseLife(){
        if(!((lifeTime % 3 == 0) || hasEaten)){
            bodySet.remove(snakeSpread.removeLast());
        }
        hasEaten = false;
        lifeTime++;
    }

    private MapCoordinate getNewPos(SnakeDirection dir) {
        switch (dir){
            case DOWN:
                return snakeSpread.getFirst().translateBy(0, 1);
            case UP:
                return snakeSpread.getFirst().translateBy(0, -1);
            case RIGHT:
                return snakeSpread.getFirst().translateBy(1, 0);
            case LEFT:
                return snakeSpread.getFirst().translateBy(-1, 0);
        }
        return snakeSpread.getFirst(); //Unreachable
    }




    public String getId() {
        return id;
    }

    public SnakeDirection getDir() {
        return dir;
    }

    public void setDir(SnakeDirection dir) {
        this.dir = dir;
    }

    private LinkedList<MapCoordinate> getSnakeSpread() {
        return snakeSpread;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public MapCoordinate getHead() {
        return snakeSpread.getFirst();
    }

    public MapCoordinate getTail() {
        return snakeSpread.getLast();
    }

    public HashSet<MapCoordinate> getBodySet(){
        return bodySet;
    }

    public int getLength(){
        return snakeSpread.size();
    }

    public boolean occupies(MapCoordinate coord){
        Boolean result;
        result = bodySet.contains(coord);
        if(result == null){
            return false; //null or false is fine
        }
        return true;
    }

    public void setHasEaten(boolean b) {
        hasEaten = b;
    }

     public boolean getHasEaten(){
        return hasEaten;
     }

    public void kill() {
         snakeSpread.clear();
         bodySet.clear();
    }
}
