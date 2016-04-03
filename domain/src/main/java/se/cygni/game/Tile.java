package se.cygni.game;

import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.WorldObject;

public class Tile {

    private final WorldObject content;

    public Tile () {
        content = new Empty();
    }

    public Tile(WorldObject content) {
        this.content = content;
    }

    public Tile(Tile copy) {
        this(copy.getContent());
    }

    public WorldObject getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "content=" + content +
                '}';
    }
}
