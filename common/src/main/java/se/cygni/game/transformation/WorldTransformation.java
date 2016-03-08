package se.cygni.game.transformation;

import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;

public interface WorldTransformation {

    WorldState transform(WorldState currentWorld) throws TransformationException;
}
