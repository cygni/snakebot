package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

import java.util.stream.IntStream;

public class KeepOnlySnakeWithId implements WorldTransformation {

    private String playerId;

    public KeepOnlySnakeWithId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
        Tile[] tiles = currentWorld.getTiles();

        IntStream.range(0, tiles.length).forEach(
                pos -> {
                    WorldObject content = tiles[pos].getContent();
                    if (content instanceof SnakePart) {
                        SnakePart snakePart = (SnakePart)content;
                        if (!playerId.equals(snakePart.getPlayerId())) {
                            tiles[pos] = new Tile();
                        }
                    }
                }
        );

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
