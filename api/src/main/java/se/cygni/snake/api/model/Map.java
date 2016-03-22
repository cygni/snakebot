package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.karaf.shell.table.Col;
import org.apache.karaf.shell.table.ShellTable;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.IntStream;

@GameMessageType
public class Map extends GameMessage {
    final int width, height;
    final long worldTick;
    final TileContent[][] tiles;
    final SnakeInfo[] snakeInfos;

    @JsonCreator
    public Map(
            @JsonProperty("width") int width,
            @JsonProperty("height") int height,
            @JsonProperty("worldTick") long worldTick,
            @JsonProperty("tiles") TileContent[][] tiles,
            @JsonProperty("snakeInfos") SnakeInfo[] snakeInfos
    )
    {
        this.width = width;
        this.height = height;
        this.worldTick = worldTick;
        this.tiles = tiles;
        this.snakeInfos = snakeInfos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TileContent[][] getTiles() {
        return tiles;
    }

    public SnakeInfo[] getSnakeInfos() {
        return snakeInfos;
    }

    public long getWorldTick() {
        return worldTick;
    }

    /**
     * Returns a printable textual representation of
     * the map.
     */
    @JsonIgnore
    public String toString() {
        ShellTable table=new ShellTable();
        table.column(new Col(" "));
        IntStream.range(0, width).forEach( w -> {
            table.column(new Col(w+""));
        });

        for (int y = 0; y < height; y++) {
            String[] rowValues = new String[width+1];
            rowValues[0] = y+"";
            for (int x = 0; x < width; x++) {
                rowValues[x+1] = tiles[x][y].toDisplay();
            }
            table.addRow().addContent((Object[])rowValues);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true);

        table.print(ps);
        String output = baos.toString();
        ps.close();
        try {
            baos.close();
        } catch (Exception e) {}
        return output;
    }
}
