import eu.lestard.grid.GridModel;
import eu.lestard.grid.GridView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.cygni.game.EventListener;
import se.cygni.game.EventSocketClient;
import se.cygni.game.MyGridModel;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.*;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.websocket.event.api.ActiveGame;
import se.cygni.snake.websocket.event.api.ActiveGamesList;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class Main extends Application implements EventListener {

    private GridModel<CellState> gridModel;
    private TextArea eventLog = new TextArea();
    private EventSocketClient eventSocketClient;
    private ListView<ActiveGame> activeGameListView;
    private ObservableList<ActiveGame> activeGames = FXCollections.observableArrayList();
    private Queue<MapUpdateEvent> mapUpdates = new ConcurrentLinkedDeque<>();
    private java.util.Map<String, java.util.Map<String, Integer>> gamePlayerColorMap = new HashMap<>();
    private GridView<CellState> gridView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

//        eventSocketClient = new EventSocketClient("ws://localhost:8080/events-native", this);
        eventSocketClient = new EventSocketClient("ws://snake.cygni.se/events-native", this);

        BorderPane root = new BorderPane();

        Node controlPane = createControlPane();
        Node eventLogPane = createEventLogPane();
        Node worldPane = createWorldPane();

        BorderPane.setAlignment(controlPane, Pos.TOP_LEFT);
        BorderPane.setAlignment(eventLogPane, Pos.CENTER);
        BorderPane.setAlignment(worldPane, Pos.CENTER);

        BorderPane.setMargin(controlPane, new Insets(12,12,12,12));
        BorderPane.setMargin(eventLogPane, new Insets(5,5,5,5));
        BorderPane.setMargin(worldPane, new Insets(12,12,12,12));

        root.setLeft(controlPane);
        root.setBottom(eventLogPane);
        root.setCenter(worldPane);

        Scene scene = new Scene(root);

        primaryStage.setWidth(700);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Snake bots");
        primaryStage.setScene(scene);
        primaryStage.show();

        logMessage("Starting...");
        logMessage("Connecting to server...");
        eventSocketClient.connect();


        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(125), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!mapUpdates.isEmpty()) {
                    if (gridView.isNeedsLayout())
                        return;

                    MapUpdateEvent mapevent = mapUpdates.poll();

                    if (mapevent.getGameTick() == 0)
                        populatePlayerColors(mapevent);

                    logMessage("Rendering game tick: " + mapevent.getGameTick());
                    populateWorldPane(mapevent, gridModel);
                    gridView.requestLayout();
                }
            }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }


    @Override
    public void onMessage(String message) {
        Platform.runLater(() -> {
            //logMessage(message);
        });
    }

    @Override
    public void onActiveGamesList(ActiveGamesList activeGamesList) {
        Platform.runLater(() -> {
            activeGames.clear();
            activeGames.addAll(activeGamesList.games);
        });
    }

    @Override
    public void onMapUpdate(MapUpdateEvent mapUpdateEvent) {
        Platform.runLater(() -> {
            mapUpdates.add(mapUpdateEvent);
            //populateWorldPane(mapUpdateEvent.getMap(), gridModel);
        });
    }

    @Override
    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent) {

    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
        System.out.println("Game ended event...");
        // Should clear game/player color map
    }

    @Override
    public void onGameStarting(GameStartingEvent gameStartingEvent) {

    }

    @Override
    public void onPlayerRegistered(PlayerRegistered playerRegistered) {

    }

    @Override
    public void onInvalidPlayerName(InvalidPlayerName invalidPlayerName) {

    }

    private void logMessage(String msg) {
        eventLog.appendText(System.lineSeparator());
        eventLog.appendText(msg);
    }

    private Node createEventLogPane() {
        eventLog.setMaxWidth(Double.MAX_VALUE);
        eventLog.setWrapText(false);
        eventLog.setPrefColumnCount(25);
        eventLog.setPrefRowCount(4);
        eventLog.setMaxWidth(Double.MAX_VALUE);
        return eventLog;
    }


    private FlowPane createControlPane() {
        FlowPane flow = new FlowPane(Orientation.VERTICAL);
        flow.setVgap(5);
        flow.setHgap(5);
        flow.setColumnHalignment(HPos.LEFT); // align labels on left
        flow.getChildren().add(new Text("Select game:"));

        activeGameListView = new ListView<>();
        activeGameListView.setPrefHeight(125);
        activeGameListView.setPrefWidth(150);

        activeGameListView.setCellFactory(cellfactory -> {
            return new ListCell<ActiveGame>() {
                @Override
                protected void updateItem(ActiveGame item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        String players = item.players.stream()
                                .map(player -> player.name)
                                .collect(Collectors.joining(", "));
                        textProperty().setValue(players);
                        tooltipProperty().setValue(new Tooltip(item.gameId));
                    } else {
                        textProperty().setValue(null);
                        tooltipProperty().setValue(null);
                    }
                }
            };
        });

        activeGameListView.setItems(activeGames);

        flow.getChildren().add(activeGameListView);

        Button startButton = new Button("Start");
        startButton.setOnAction(event -> {
            ActiveGame game = activeGameListView.getSelectionModel().getSelectedItem();

            if (game == null) {
                return;
            }
            startGame(game);
        });

        flow.getChildren().add(startButton);
        return flow;
    }

    private GridView createWorldPane() {
        gridView = new GridView<>();

        gridView.addColorMapping(CellState.EMPTY, Color.WHITE);
        gridView.addColorMapping(CellState.FOOD, Color.DARKGREEN);
        gridView.addColorMapping(CellState.OBSTACLE, Color.BLACK);

        gridView.addColorMapping(CellState.SNAKE1HEAD, Color.AQUA);
        gridView.addColorMapping(CellState.SNAKE1BODY, Color.RED);
        gridView.addColorMapping(CellState.SNAKE1TAIL0, createColorWithOpacity(Color.RED, 0.8));
        gridView.addColorMapping(CellState.SNAKE1TAIL1, createColorWithOpacity(Color.RED, 0.6));
        gridView.addColorMapping(CellState.SNAKE1TAIL2, createColorWithOpacity(Color.RED, 0.4));
        gridView.addColorMapping(CellState.SNAKE1TAIL3, createColorWithOpacity(Color.RED, 0.2));

        gridView.addColorMapping(CellState.SNAKE2HEAD, Color.MAROON);
        gridView.addColorMapping(CellState.SNAKE2BODY, Color.LIGHTSKYBLUE);
        gridView.addColorMapping(CellState.SNAKE2TAIL0, createColorWithOpacity(Color.LIGHTSKYBLUE, 0.8));
        gridView.addColorMapping(CellState.SNAKE2TAIL1, createColorWithOpacity(Color.LIGHTSKYBLUE, 0.6));
        gridView.addColorMapping(CellState.SNAKE2TAIL2, createColorWithOpacity(Color.LIGHTSKYBLUE, 0.4));
        gridView.addColorMapping(CellState.SNAKE2TAIL3, createColorWithOpacity(Color.LIGHTSKYBLUE, 0.2));


        gridView.addColorMapping(CellState.SNAKE3HEAD, Color.BLUE);
        gridView.addColorMapping(CellState.SNAKE3BODY, Color.BLANCHEDALMOND);
        gridView.addColorMapping(CellState.SNAKE3TAIL0, createColorWithOpacity(Color.BLANCHEDALMOND, 0.8));
        gridView.addColorMapping(CellState.SNAKE3TAIL1, createColorWithOpacity(Color.BLANCHEDALMOND, 0.6));
        gridView.addColorMapping(CellState.SNAKE3TAIL2, createColorWithOpacity(Color.BLANCHEDALMOND, 0.4));
        gridView.addColorMapping(CellState.SNAKE3TAIL3, createColorWithOpacity(Color.BLANCHEDALMOND, 0.2));


        gridView.addColorMapping(CellState.SNAKE4HEAD, Color.CRIMSON);
        gridView.addColorMapping(CellState.SNAKE4BODY, Color.ORCHID);
        gridView.addColorMapping(CellState.SNAKE4TAIL0, createColorWithOpacity(Color.ORCHID, 0.8));
        gridView.addColorMapping(CellState.SNAKE4TAIL1, createColorWithOpacity(Color.ORCHID, 0.6));
        gridView.addColorMapping(CellState.SNAKE4TAIL2, createColorWithOpacity(Color.ORCHID, 0.4));
        gridView.addColorMapping(CellState.SNAKE4TAIL3, createColorWithOpacity(Color.ORCHID, 0.2));


        gridView.addColorMapping(CellState.SNAKE5HEAD, Color.FUCHSIA);
        gridView.addColorMapping(CellState.SNAKE5BODY, Color.CORNFLOWERBLUE);
        gridView.addColorMapping(CellState.SNAKE5TAIL0, createColorWithOpacity(Color.CORNFLOWERBLUE, 0.8));
        gridView.addColorMapping(CellState.SNAKE5TAIL1, createColorWithOpacity(Color.CORNFLOWERBLUE, 0.6));
        gridView.addColorMapping(CellState.SNAKE5TAIL2, createColorWithOpacity(Color.CORNFLOWERBLUE, 0.4));
        gridView.addColorMapping(CellState.SNAKE5TAIL3, createColorWithOpacity(Color.CORNFLOWERBLUE, 0.2));

        gridView.cellBorderWidthProperty().set(0.5);

        gridView.setGridModel(getGridModel());
        return gridView;
    }

    private Color createColorWithOpacity(Color c, double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }

    private GridModel<CellState> getGridModel() {
        gridModel = new MyGridModel<>();
        gridModel.setDefaultState(CellState.EMPTY);

        gridModel.setNumberOfColumns(3);
        gridModel.setNumberOfRows(3);

        return gridModel;
    }


    private void populateWorldPane(MapUpdateEvent mapUpdateEvent, GridModel<CellState> gridModel) {

        Map map = mapUpdateEvent.getMap();

        // This below is needed for the correct initialization of
        // the GridModel. It's a clonky implementation, not suited
        // for large grids.
        if (gridModel.getNumberOfColumns() != map.getWidth()) {
            gridModel.cells().clear();
            gridModel.setNumberOfColumns(map.getWidth());
        }
        if (gridModel.getNumberOfRows() != map.getHeight()) {
            gridModel.cells().clear();
            gridModel.setNumberOfRows(map.getHeight());
        }

        String gameId = mapUpdateEvent.getGameId();
        MapUtil mapUtil = new MapUtil(mapUpdateEvent.getMap(), "fake");

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                CellState cellState = null;

                TileContent content = map.getTiles()[x][y];
                if (content instanceof MapFood) {
                    cellState = CellState.FOOD;
                }
                if (content instanceof MapObstacle) {
                    cellState = CellState.OBSTACLE;
                }
                if (content instanceof MapSnakeHead) {
                    cellState = getCellState(gameId, (MapSnakeHead)content);
                }
                if (content instanceof MapSnakeBody) {
                    cellState = getCellState(gameId, (MapSnakeBody)content, mapUpdateEvent, mapUtil);
                }

                if (gridModel.getCell(x,y).getState() != cellState)
                    gridModel.getCell(x,y).changeState(cellState);
            }
        }
    }

    private void populatePlayerColors(MapUpdateEvent mapUpdateEvent) {

        String gameId = mapUpdateEvent.getGameId();
        gamePlayerColorMap.put(gameId,
                new HashMap<>());

        int c = 1;
        for (SnakeInfo snakeInfo : mapUpdateEvent.getMap().getSnakeInfos()) {
            gamePlayerColorMap.get(gameId).put(snakeInfo.getId(), c++);
        }
    }

    private CellState getCellState(String gameId, MapSnakeHead snakeHead) {

        int index = gamePlayerColorMap.get(gameId).get(snakeHead.getPlayerId());
        return CellState.valueOf("SNAKE" + index + "HEAD");
    }

    private CellState getCellState(String gameId, MapSnakeBody snakeBody, MapUpdateEvent mapUpdateEvent, MapUtil mapUtil) {

        int length = mapUtil.getPlayerLength(snakeBody.getPlayerId());
        int currBodyPartOrder = snakeBody.getOrder();

        int tailOrder = currBodyPartOrder - length + 3;
        boolean isTail = false;
        if (tailOrder >= 0) {
            isTail = true;
        }
        int index = gamePlayerColorMap.get(gameId).get(snakeBody.getPlayerId());
        if (!isTail)
            return CellState.valueOf("SNAKE" + index + "BODY");

        return CellState.valueOf("SNAKE" + index + "TAIL" + tailOrder);
    }

    private void startGame(ActiveGame game) {
        eventSocketClient.setGameIdFilter(game.gameId);
        eventSocketClient.startGame(game.gameId);
    }

    private Map createMap() {
        TileContent[][] contents = new TileContent[][] {
                {new MapEmpty(), new MapEmpty(), new MapEmpty()},
                {new MapFood(), new MapEmpty(), new MapEmpty()},
                {new MapEmpty(), new MapFood(), new MapObstacle()}
        };
        Map map = new Map(3, 3, 0, contents, null);
        return map;
    }
}
