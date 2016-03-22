package se.cygni.game.worldobject;

public class SnakeBody implements SnakePart {

    private SnakePart nextSnakePart = null;
    private int position;
    private String playerId;

    public SnakeBody(String playerId, SnakePart nextSnakePart, int position) {
        this.playerId = playerId;
        this.nextSnakePart = nextSnakePart;
        this.position = position;
    }

    public SnakeBody(String playerId, int position) {
        this.playerId = playerId;
        this.position = position;
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public SnakePart getNextSnakePart() {
        return nextSnakePart;
    }

    @Override
    public void setNextSnakePart(SnakePart nextSnakePart) {
        this.nextSnakePart = nextSnakePart;
    }

    @Override
    public boolean isHead() {
        return false;
    }

    @Override
    public boolean isTail() {
        return getNextSnakePart() == null;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }
}
