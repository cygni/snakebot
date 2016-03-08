package se.cygni.game.exception;

public class ObstacleCollision extends CollisionException {

    public ObstacleCollision(int position) {
        super(position);
    }
}
