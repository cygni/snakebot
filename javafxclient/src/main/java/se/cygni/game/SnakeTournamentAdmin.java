package se.cygni.game;

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
import se.cygni.game.render.BoardPane;
import se.cygni.game.render.HighscorePane;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.eventapi.model.ActiveGame;
import se.cygni.snake.eventapi.request.CreateTournament;
import se.cygni.snake.eventapi.request.KillTournament;
import se.cygni.snake.eventapi.request.StartTournament;
import se.cygni.snake.eventapi.response.ActiveGamesList;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class SnakeTournamentAdmin extends Application implements EventListener {

    private TextArea eventLog = new TextArea();
    private BoardPane boardPane;
    private HighscorePane highscorePane;
    private EventSocketClient eventSocketClient;
    private ListView<ActiveGame> activeGameListView;
    private ObservableList<ActiveGame> activeGames = FXCollections.observableArrayList();
    private Queue<GameMessage> mapUpdates = new ConcurrentLinkedDeque<>();
    private java.util.Map<String, java.util.Map<String, Integer>> gamePlayerColorMap = new HashMap<>();

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

        BorderPane.setMargin(controlPane, new Insets(12, 12, 12, 12));
        BorderPane.setMargin(eventLogPane, new Insets(5, 5, 5, 5));
        BorderPane.setMargin(worldPane, new Insets(12, 12, 12, 12));

        root.setLeft(controlPane);
        root.setBottom(eventLogPane);
        root.setCenter(worldPane);

        Scene scene = new Scene(root);

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Snake bots");
        primaryStage.setScene(scene);
        primaryStage.show();

        logMessage("Starting...");
        logMessage("Connecting to server...");
        eventSocketClient.connect();


        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(25), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!mapUpdates.isEmpty()) {
                    if (boardPane.isNeedsLayout())
                        return;

                    GameMessage message = mapUpdates.poll();

                    Map map = null;
                    long gameTick = 0;

                    if (message instanceof MapUpdateEvent) {
                        map = ((MapUpdateEvent)message).getMap();
                        gameTick = ((MapUpdateEvent)message).getGameTick();
                    } else if (message instanceof GameEndedEvent) {
                        map = ((GameEndedEvent)message).getMap();
                        gameTick = ((GameEndedEvent)message).getGameTick();
                    }

                    if (map == null) {
                        return;
                    }

                    logMessage("Rendering game tick: " + gameTick);
                    boardPane.drawMapUpdate(map);
                    highscorePane.setSnakeInfos(map.getSnakeInfos(), boardPane.getSnakeColorMap());
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
        });
    }

    @Override
    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent) {
        Platform.runLater(() -> {
            logMessage(String.format("Snake: %s died by: %s",
                    snakeDeadEvent.getPlayerId(),
                    snakeDeadEvent.getDeathReason().toString()));
        });
    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
        Platform.runLater(() -> {
            logMessage(String.format("Game ended, winner: %s",
                    gameEndedEvent.getPlayerWinnerId()));

            System.out.println("Game ended event...");
            mapUpdates.add(gameEndedEvent);
            // Should clear game/player color map
        });
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

        Button startButton = new Button("Start G");
        startButton.setOnAction(event -> {
            ActiveGame game = activeGameListView.getSelectionModel().getSelectedItem();

            if (game == null) {
                return;
            }
            startGame(game);
        });
        flow.getChildren().add(startButton);

        Button subscribetBtn = new Button("Subscribe");
        subscribetBtn.setOnAction(event -> {
            subscribe();
        });
        flow.getChildren().add(subscribetBtn);

        Button createTornamentBtn = new Button("Create T");
        createTornamentBtn.setOnAction(event -> {
            createTournament();
        });
        flow.getChildren().add(createTornamentBtn);

        Button killTournamentBtn = new Button("Kill T");
        killTournamentBtn.setOnAction(event -> {
            killTournament();
        });
        flow.getChildren().add(killTournamentBtn);

        Button startTournamentBtn = new Button("Start T");
        startTournamentBtn.setOnAction(event -> {
            startTournament();
        });
        flow.getChildren().add(startTournamentBtn);

        highscorePane = new HighscorePane();
        flow.getChildren().add(highscorePane);

        return flow;
    }

    private Node createWorldPane() {
        boardPane = new BoardPane(Color.WHITE);
        return boardPane;
    }

    private void startGame(ActiveGame game) {
        eventSocketClient.setGameIdFilter(game.gameId);
        eventSocketClient.startGame(game.gameId);
    }

    private void subscribe() {
        ActiveGame game = activeGameListView.getSelectionModel().getSelectedItem();

        if (game == null) {
            return;
        }
        eventSocketClient.setGameIdFilter(game.gameId);
    }

    private void killTournament() {
        eventSocketClient.sendApiMesssage(new KillTournament("fake", "null"));
    }

    private void createTournament() {
        eventSocketClient.sendApiMesssage(new CreateTournament("fake", "My Tournament"));
    }

    private void startTournament() {
        eventSocketClient.sendApiMesssage(new StartTournament("fake", "null"));
    }
}
