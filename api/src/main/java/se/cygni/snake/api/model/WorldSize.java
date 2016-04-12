package se.cygni.snake.api.model;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public enum WorldSize {
    SMALL(25),
    MEDIUM(50),
    LARGE(75),
    XLARGE(100);

    private final int size;

    WorldSize(final int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    /**
     * @param size size to convert to an Enum
     * @return the appropriate Enum, or MEDIUM if size is 'not valid'
     */
    public static WorldSize getForSize(final int size) {
        for (WorldSize worldSize : WorldSize.values()) {
            if (worldSize.getSize() == size) {
                return worldSize;
            }
        }
        return WorldSize.MEDIUM;
    }
}
