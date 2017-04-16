package se.cygni.snake.client;

import se.cygni.snake.api.model.SnakeDirection;

public class MapCoordinate {
    public final int x;
    public final int y;

    public MapCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MapCoordinate translateByDirection(SnakeDirection direction) {
        switch (direction) {
            case DOWN:
                return translateBy(0, 1);
            case UP:
                return translateBy(0, -1);
            case LEFT:
                return translateBy(-1, 0);
            case RIGHT:
                return translateBy(1, 0);
            default:
                return translateBy(0, 0);
        }
    }

    public MapCoordinate translateBy(int deltaX, int deltaY) {
        return new MapCoordinate(x + deltaX, y + deltaY);
    }

    public int getManhattanDistanceTo(MapCoordinate coordinate) {
        return Math.abs(x - coordinate.x) + Math.abs(y - coordinate.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCoordinate that = (MapCoordinate) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "MapCoordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
