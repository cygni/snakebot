package se.cygni.game.exception;

public class WallCollision extends CollisionException {

    public WallCollision(int position) {
        super(position);
    }
}
