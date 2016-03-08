package se.cygni.game.exception;

public class SnakeCollision extends CollisionException {

    public SnakeCollision(int position) {
        super(position);
    }
}
