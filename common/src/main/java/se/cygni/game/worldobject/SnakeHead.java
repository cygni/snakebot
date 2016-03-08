package se.cygni.game.worldobject;


import se.cygni.game.enums.Direction;

public class SnakeHead implements SnakePart {

    private final String name;
    private final String playerId;
    private int position;
    private boolean alive;
    private Direction lastDirection;
    private SnakePart nextSnakePart = null;

    public SnakeHead(String name, String playerId, int position) {
        this.name = name;
        this.playerId = playerId;
        this.position = position;
        this.alive = true;
    }

    @Override
    public boolean isHead() {
        return true;
    }

    @Override
    public boolean isTail() {
        return getNextSnakePart() == null;
    }

    @Override
    public SnakePart getNextSnakePart() {
        return nextSnakePart;
    }

    @Override
    public void setNextSnakePart(SnakePart nextSnakePart) {
        this.nextSnakePart = nextSnakePart;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        int length = 0;
        SnakePart part = this;
        while (part != null) {
            length++;
            part = part.getNextSnakePart();
        }

        return length;
    }

    public String getPlayerId() {
        return playerId;
    }
}
