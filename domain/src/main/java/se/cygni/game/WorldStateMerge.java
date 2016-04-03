package se.cygni.game;

import java.util.stream.IntStream;

public class WorldStateMerge {

    private final int width, height;
    private final TileMultipleContent[] tiles;
    private int noofMerges = 0;

    public WorldStateMerge(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = createEmptyTiles();
    }

    public WorldStateMerge(int width, int height, Tile[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = convertTiles(tiles);
    }

    public WorldStateMerge(WorldState worldState) {
        this.width = worldState.getWidth();
        this.height = worldState.getHeight();
        this.tiles = convertTiles(worldState.getTiles());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return width * height;
    }

    public void mergeWith(WorldState ws) {
        Tile[] mergeTiles = ws.getTiles();

        if (mergeTiles.length != tiles.length) {
            throw new RuntimeException("World size not matching, cannot merge.");
        }

        IntStream.range(0, mergeTiles.length).forEach(
                pos -> {
                    tiles[pos].addContent(mergeTiles[pos].getContent());
                }
        );

        noofMerges++;
    }

    private TileMultipleContent[] convertTiles(Tile[] tiles) {
        TileMultipleContent[] newTiles = new TileMultipleContent[tiles.length];

        IntStream.range(0, tiles.length).forEach(
                pos -> {
                    newTiles[pos] = new TileMultipleContent(tiles[pos].getContent());
                }
        );

        return newTiles;
    }

    /**
     * The world is represented by a single array
     */
    private TileMultipleContent[] createEmptyTiles() {
        int size = getSize();
        TileMultipleContent[] tiles = new TileMultipleContent[size];

        IntStream.range(0, size).forEach(
                pos -> {
                    tiles[pos] = new TileMultipleContent();
                }
        );
        return tiles;
    }
}
