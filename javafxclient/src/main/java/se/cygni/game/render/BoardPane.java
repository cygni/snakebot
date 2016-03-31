package se.cygni.game.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

public class BoardPane extends Pane {

    private Canvas canvas = new Canvas();
    private Color background;
    private double lineWidth = 1.0;
    private MapUpdateEvent lastMapUpdateEvent = null;

    public BoardPane(Color background) {
        this.background = background;
        getChildren().add(canvas);
    }

    public void drawMapUpdate(MapUpdateEvent mapUpdateEvent) {
        lastMapUpdateEvent = mapUpdateEvent;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawGrid(gc, mapUpdateEvent.getMap());

        MapUtil mapUtil = new MapUtil(mapUpdateEvent.getMap(), "fake");

        for (SnakeInfo snakeInfo : mapUpdateEvent.getMap().getSnakeInfos()) {
            drawSnake(
                    gc,
                    mapUtil.getSnakeSpread(snakeInfo.getId()),
                    mapUpdateEvent.getMap(),
                    Color.BLUEVIOLET, Color.BROWN);
        }

        drawStaticObjects(gc, mapUtil, mapUpdateEvent.getMap(), Color.LIGHTGREEN, Color.BLACK);

    }

    private double getActualWidth() {
        return getWidth() - snappedLeftInset() - snappedRightInset();
    }

    private double getActualHeight() {
        return getHeight() - snappedTopInset() - snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;

        canvas.setLayoutX(snappedLeftInset());
        canvas.setLayoutY(snappedTopInset());

        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.clearRect(0, 0, w, h);

            g.setFill(background);
            g.fillRect(0, 0, w, h);

            if (lastMapUpdateEvent != null) {
                drawMapUpdate(lastMapUpdateEvent);
            }
        }
    }

    private void drawGrid(GraphicsContext gc, Map map) {

        double width = getActualWidth();
        double height = getActualHeight();

        // Clear the canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, width, height);

        double tileWidth = (width + lineWidth - map.getWidth() * lineWidth) / (double)map.getWidth();
        double tileHeight = (height + lineWidth - map.getHeight() * lineWidth) / (double)map.getHeight();

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(lineWidth);

        // Vertical lines
        double vPos = tileWidth;
        while (vPos < width) {
            gc.strokeLine(vPos, 0, vPos, height);
            vPos += tileWidth + lineWidth;
        }

        // Horizontal lines
        double hPos = tileWidth;
        while (hPos < width) {
            gc.strokeLine(0, hPos, width, hPos);
            hPos += tileHeight + lineWidth;
        }
    }

    private void drawSnake(GraphicsContext gc, MapCoordinate[] snakeSpread, Map map, Color head, Color body) {
        double width = getActualWidth();
        double height = getActualHeight();

        double tileWidth = (width + lineWidth - map.getWidth() * lineWidth) / (double)map.getWidth();
        double tileHeight = (height + lineWidth - map.getHeight() * lineWidth) / (double)map.getHeight();

        boolean isHead = true;
        for (MapCoordinate coordinate : snakeSpread) {
            double x = coordinate.x * tileWidth + (coordinate.x-1) * lineWidth;
            double y = coordinate.y * tileHeight + (coordinate.y) * lineWidth;

            if (isHead) {
                gc.setFill(head);
            } else {
                gc.setFill(body);
            }

            gc.fillRect(x, y, tileWidth, tileHeight);
            isHead = false;
        }
    }

    private void drawStaticObjects(GraphicsContext gc, MapUtil mapUtil, Map map, Color food, Color obstacle) {
        double width = getActualWidth();
        double height = getActualHeight();

        double tileWidth = (width + lineWidth - map.getWidth() * lineWidth) / (double)map.getWidth();
        double tileHeight = (height + lineWidth - map.getHeight() * lineWidth) / (double)map.getHeight();

        for (MapCoordinate coordinate : mapUtil.listCoordinatesContainingFood()) {
            double x = coordinate.x * tileWidth + (coordinate.x-1) * lineWidth;
            double y = coordinate.y * tileHeight + (coordinate.y) * lineWidth;
            gc.setFill(food);
            gc.fillRect(x, y, tileWidth, tileHeight);
        }

        for (MapCoordinate coordinate : mapUtil.listCoordinatesContainingObstacle()) {
            double x = coordinate.x * tileWidth + (coordinate.x-1) * lineWidth;
            double y = coordinate.y * tileHeight + (coordinate.y) * lineWidth;
            gc.setFill(obstacle);
            gc.fillRect(x, y, tileWidth, tileHeight);
        }
    }
}
