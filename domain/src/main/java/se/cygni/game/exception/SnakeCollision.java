package se.cygni.game.exception;

import se.cygni.game.worldobject.SnakeHead;

public class SnakeCollision extends CollisionException {

    private final SnakeHead collisionWith;

    public SnakeCollision(int position, SnakeHead head) {
        super(position);
        this.collisionWith = head;
    }

    public SnakeHead getCollisionWith() {
        return collisionWith;
    }
}
