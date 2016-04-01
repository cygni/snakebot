package se.cygni.game.render;

import javafx.scene.paint.Color;

public class SnakeColor {
    public final Color head;
    public final Color body;
    public final Color tail1;
    public final Color tail2;
    public final Color tail3;
    public final Color tail4;

    public SnakeColor(Color head, Color body) {
        this.head = head;
        this.body = body;

        this.tail4 = createColorWithOpacity(body, 0.8);
        this.tail3 = createColorWithOpacity(body, 0.6);
        this.tail2 = createColorWithOpacity(body, 0.4);
        this.tail1 = createColorWithOpacity(body, 0.2);

    }

    private Color createColorWithOpacity(Color c, double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }
}
