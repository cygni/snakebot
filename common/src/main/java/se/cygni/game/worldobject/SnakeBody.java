package se.cygni.game.worldobject;

public class SnakeBody implements SnakePart {

    private SnakePart nextSnakePart = null;
    private int position;

    public SnakeBody() {
    }

    public SnakeBody(SnakePart nextSnakePart) {
        this.nextSnakePart = nextSnakePart;
    }

    public SnakeBody(int position) {
        this.position = position;
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
