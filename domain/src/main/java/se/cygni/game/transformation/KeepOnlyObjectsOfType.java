package se.cygni.game.transformation;

import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.WorldObject;

public class KeepOnlyObjectsOfType implements WorldTransformation {

    private final Class<WorldObject>[] types;

    @SafeVarargs
    public KeepOnlyObjectsOfType(Class<WorldObject>... types) {
	this.types = types;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
	final Tile[] tiles = currentWorld.getTiles();

	IntStream.range(0, tiles.length).forEach(pos -> {
	    final WorldObject content = tiles[pos].getContent();
	    if (!ArrayUtils.contains(types, content.getClass())) {
		tiles[pos] = new Tile();
	    }
	});

	return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
