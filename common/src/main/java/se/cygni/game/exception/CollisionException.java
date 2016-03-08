
package se.cygni.game.exception;

public class CollisionException extends TransformationException {

    private final int position;

    public CollisionException(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
