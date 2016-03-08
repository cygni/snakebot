package se.cygni.snake.client;

import org.apache.commons.lang3.StringUtils;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.*;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class AnsiPrinter {
    int width, height;
    private HashMap<String, String> playerColorMap = new HashMap<>();
    private boolean includeLegend = true;

    public static String SNAKE_HEAD_COLOR = "[33m"; // fg_Yellow;
    public static String FOOD_COLOR = "[32m"; // fg_Green;
    public static String OBSTACLE_COLOR = "[30m"; // fg_Black;

    public AnsiPrinter() {

    }

    public AnsiPrinter(boolean includeLegend) {
        this.includeLegend = true;
    }

    public static void main(String[] args) throws Exception {
        MapUpdateEvent event = (MapUpdateEvent)GameMessageParser.decodeMessage(mapBug);

        AnsiPrinter printer = new AnsiPrinter();
        printer.printMap(event);
    }

    public void printMap(MapUpdateEvent event) {
        if (event.getMap().getSnakeInfos().length > event.getMap().getHeight()-5) {
            System.out.println("Sorry, too many snakes I can't render this.");
            return;
        }

        if (event.getMap().getSnakeInfos().length > 11) {
            System.out.println("Sorry, too many snakes I can't render this.");
            return;
        }

        if (event.getMap().getWidth() > 120) {
            System.out.println("Sorry, the map is too wide, I can't render this.");
            return;
        }

        if (event.getMap().getHeight() > 120) {
            System.out.println("Sorry, the map is too high, I can't render this.");
            return;
        }

        populateSnakeColors(event);
        printMapActual(event);
    }

    private void printMapActual(MapUpdateEvent event) {

        Map map = event.getMap();

        this.width = map.getWidth();
        this.height = map.getHeight();

        StringBuilder sb = new StringBuilder();

        sb.append("Game id: ").append(event.getGameId())
                .append("\n")
                .append("Game tick: ").append(event.getGameTick())
                .append("\n\n");

        TileContent[][] tiles = map.getTiles();

        for (int y = 0; y < height; y++) {
            TileContent[] row = new TileContent[width];
            for (int x = 0; x < width; x++) {
                row[x] = tiles[x][y];
            }
            printRow(row, event, sb);
            appendLegendForRow(y, map, sb);
        }

        System.out.println(sb);
    }

    private Queue<String> getAvailableColors() {
        Queue<String> availableColors = new ArrayDeque<>();
        availableColors.add(fg_Light_yellow);
        availableColors.add(fg_Light_blue);
        availableColors.add(fg_Light_cyan);
        availableColors.add(fg_Light_red);
        availableColors.add(fg_Light_magenta);
        availableColors.add(fg_Blue);
        availableColors.add(fg_Cyan);
        availableColors.add(fg_Magenta);
        availableColors.add(fg_White);
        availableColors.add(fg_Red);
        return availableColors;
    }

    private void populateSnakeColors(MapUpdateEvent event) {
        Queue<String> availableColors = getAvailableColors();

        for (SnakeInfo snakeInfo : event.getMap().getSnakeInfos()) {
            if (!playerColorMap.containsKey(snakeInfo.getId())) {

                if (snakeInfo.getId().equals(event.getReceivingPlayerId())) {
                    playerColorMap.put(snakeInfo.getId(), fg_Light_green);
                } else {
                    playerColorMap.put(snakeInfo.getId(), availableColors.remove());
                }
            }
        }
    }

    private void appendLegendForRow(int row, Map map, StringBuilder sb) {
        String indent = "    ";
        int offset = getOffsetForLegend(map);
        int noofStdItems = 4;

        int noofPlayers = map.getSnakeInfos().length;

        if (!includeLegend || row < offset || row-offset > noofPlayers + noofStdItems) {
            sb.append("\n");
            return;
        }

        if (row == offset) {
            sb.append("   LEGEND:\n");
            return;
        }

        int stdOffset = offset + noofPlayers;

        if (row > stdOffset) {
            int k = row - offset - noofPlayers - 1;
            switch (k) {
                case 1: sb.append(indent)
                        .append((char)27).append(SNAKE_HEAD_COLOR).append(SNAKE_PART)
                        .append((char)27).append(fg_Default)
                        .append(" ").append("Snake head")
                        .append("\n"); return;
                case 2: sb.append(indent)
                        .append((char)27).append(FOOD_COLOR).append(FOOD)
                        .append((char)27).append(fg_Default)
                        .append(" ").append("Food")
                        .append("\n"); return;
                case 3: sb.append(indent)
                        .append((char)27).append(OBSTACLE_COLOR).append(OBSTACLE)
                        .append((char)27).append(fg_Default)
                        .append(" ").append("Obstacle")
                        .append("\n"); return;
            }
            sb.append("\n");
            return;
        }

        SnakeInfo si = map.getSnakeInfos()[row-offset-1];
        sb.append(indent)
                .append((char)27).append(getSnakeColor(si.getId())).append(SNAKE_PART)
                .append((char)27).append(fg_Default)
                .append(" ").append(si.getName())
                .append(" (l: ").append(si.getLength()).append(")")
//                .append(" (p: ").append(si.getPoints()).append(")")
                .append("\n");
    }

    private int getOffsetForLegend(Map map) {
        return 2;
    }

    private String getSnakeColor(String id) {
        return playerColorMap.get(id);
    }

    private void printRow(TileContent[] row, MapUpdateEvent event, StringBuilder sb) {
        String bg_color = bg_Light_gray;
        String bg_default = bg_Default;

        for (TileContent tc : row) {
            if (tc instanceof MapSnakeBody)
                append(getSnakeColor(((MapSnakeBody)tc).getPlayerId()), bg_color, SNAKE_PART, fg_Default, bg_default, sb);
            else if (tc instanceof MapSnakeHead) {
                if (getSnakeLength(((MapSnakeHead)tc).getPlayerId(), event.getMap().getSnakeInfos()) > 1)
                    append(SNAKE_HEAD_COLOR, bg_color, SNAKE_PART, fg_Default, bg_default, sb);
                else
                    append(getSnakeColor(((MapSnakeHead)tc).getPlayerId()), bg_color, SNAKE_PART, fg_Default, bg_default, sb);
            }
            else if (tc instanceof MapFood)
                append(FOOD_COLOR, bg_color, FOOD, fg_Default, bg_default, sb);
            else if (tc instanceof MapObstacle)
                append(OBSTACLE_COLOR, bg_color, OBSTACLE, fg_Default, bg_default, sb);
            else
                append(bg_Light_gray, bg_color, EMPTY, fg_Default, bg_default, sb);
        }
    }

    private int getSnakeLength(String playerId, SnakeInfo[] snakeInfos) {
        for (SnakeInfo si : snakeInfos) {
            if (si.getId().equals(playerId))
                return si.getLength();
        }
        return 0;
    }

    private void append(String fgcolor, String bgcolor, String text, String resetFgColor, String resetBgColor, StringBuilder sb) {
        if (StringUtils.isNotEmpty(fgcolor))
            sb.append((char)27).append(fgcolor);
        if (StringUtils.isNotEmpty(bgcolor))
            sb.append((char)27).append(bgcolor);

        sb.append(text);

        if (StringUtils.isNotEmpty(resetFgColor))
            sb.append((char)27).append(resetFgColor);
        if (StringUtils.isNotEmpty(resetBgColor))
            sb.append((char)27).append(resetBgColor);
    }

    public static String EMPTY = "  ";
    public static String FOOD  = "██";
    public static String OBSTACLE  = "██";
    public static String SNAKE_PART = "██";

    public static String fg_Default = "[39m";
    public static String fg_Black = "[30m";
    public static String fg_Red = "[31m";
    public static String fg_Green = "[32m";
    public static String fg_Yellow = "[33m";
    public static String fg_Blue = "[34m";
    public static String fg_Magenta = "[35m";
    public static String fg_Cyan = "[36m";
    public static String fg_Light_gray = "[37m";
    public static String fg_Dark_gray = "[90m";
    public static String fg_Light_red = "[91m";
    public static String fg_Light_green = "[92m";
    public static String fg_Light_yellow = "[93m";
    public static String fg_Light_blue = "[94m";
    public static String fg_Light_magenta = "[95m";
    public static String fg_Light_cyan = "[96m";
    public static String fg_White = "[97m";

    public static String bg_Default = "[49m";
    public static String bg_Light_gray = "[47m";

    private static String mapBug = "{\"gameTick\":1,\"gameId\":\"e1060d4a-6ec6-4265-8fc8-ed4fdaa2a691\",\"map\":{\"width\":25,\"height\":25,\"worldTick\":1,\"tiles\":[[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"name\":\"#emil\",\"playerId\":\"a2e312ca-3c8f-4988-9d09-e918dc8d459d\",\"content\":\"snakehead\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"name\":null,\"playerId\":\"c16eae98-6d83-4518-9293-0fb3c41b28b7\",\"content\":\"snakehead\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"name\":null,\"playerId\":\"15919131-5f6e-4931-8f12-cba68a900462\",\"content\":\"snakehead\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}]],\"snakeInfos\":[{\"name\":null,\"length\":1,\"x\":20,\"y\":1,\"id\":\"c16eae98-6d83-4518-9293-0fb3c41b28b7\"},{\"name\":\"#emil\",\"length\":1,\"x\":3,\"y\":13,\"id\":\"a2e312ca-3c8f-4988-9d09-e918dc8d459d\"},{\"name\":null,\"length\":1,\"x\":24,\"y\":15,\"id\":\"15919131-5f6e-4931-8f12-cba68a900462\"}],\"receivingPlayerId\":null,\"type\":\"se.cygni.snake.api.model.Map\"},\"receivingPlayerId\":\"a2e312ca-3c8f-4988-9d09-e918dc8d459d\",\"type\":\"se.cygni.snake.api.event.MapUpdateEvent\"}";
    private static String map = "{\"gameTick\":17,\"gameId\":\"636281eb-e09e-4d94-92ae-79098d9369bf\",\"map\":{\"width\":25,\"height\":25,\"worldTick\":17,\"tiles\":[[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"name\":null,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"content\":\"snakehead\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":1,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":2,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":1,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"name\":null,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"content\":\"snakehead\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":3,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":2,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":4,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":3,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":5,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":4,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":6,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":5,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":false,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":7,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":6,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":true,\"playerId\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\",\"order\":8,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":false,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":7,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"tail\":true,\"playerId\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\",\"order\":8,\"content\":\"snakebody\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"tail\":true,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":8,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":7,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":6,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":5,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":4,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":3,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":2,\"content\":\"snakebody\"},{\"tail\":false,\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"order\":1,\"content\":\"snakebody\"},{\"name\":\"#emil\",\"playerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"content\":\"snakehead\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"food\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}],[{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"},{\"content\":\"empty\"}]],\"snakeInfos\":[{\"name\":null,\"length\":9,\"x\":1,\"y\":0,\"id\":\"9f5df1b1-8ec4-4342-9fd5-74cc57b1b9a9\"},{\"name\":null,\"length\":9,\"x\":2,\"y\":12,\"id\":\"e05ed44c-ce9d-44d9-a7fd-aeb48e325a84\"},{\"name\":\"#emil\",\"length\":9,\"x\":13,\"y\":24,\"id\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\"}],\"receivingPlayerId\":null,\"type\":\"se.cygni.snake.api.model.Map\"},\"receivingPlayerId\":\"dfdd6db9-73e0-494e-9b81-ae7bc873696b\",\"type\":\"se.cygni.snake.api.event.MapUpdateEvent\"}\n";
}
