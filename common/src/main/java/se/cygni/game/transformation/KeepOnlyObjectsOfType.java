package se.cygni.game.transformation;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.WorldObject;

import java.util.stream.IntStream;

public class KeepOnlyObjectsOfType implements WorldTransformation {

    private Class<WorldObject>[] types;

    public KeepOnlyObjectsOfType(Class<WorldObject>...types) {
        this.types = types;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
        Tile[] tiles = currentWorld.getTiles();

        IntStream.range(0, tiles.length).forEach(
                pos -> {
                    WorldObject content = tiles[pos].getContent();
                    if (!ArrayUtils.contains(types, content.getClass())) {
                        tiles[pos] = new Tile();
                    }
                }
        );

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
