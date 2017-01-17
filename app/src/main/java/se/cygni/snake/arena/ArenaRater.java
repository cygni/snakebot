package se.cygni.snake.arena;

import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.game.Game;
import se.cygni.snake.player.IPlayer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaRater {
    private static final Logger log = LoggerFactory.getLogger(ArenaRater.class);

    private Map<String, Long> rating = new HashMap<>();
    private List<GameResult> gameResults = new ArrayList<>();

    public static class GameResult {
        LocalDate date;
        String gameId;
        List<String> positions = new ArrayList<>();
    }

    public Map<String, Long> getRating() {
        return rating;
    }

    public List<GameResult> getGameResults() {
        return gameResults;
    }

    public void initializeWithPersistedGameResults() {
        // TODO ArenaRater is in memory only for now, but should probably be initialized with game history on startup
    }

    public void addGameToResult(Game game, boolean ranked) {
        GameResult res = new GameResult();
        res.date = LocalDate.now();
        res.positions = game.getGameResult()
                .getSortedResult()
                .stream()
                .map(IPlayer::getName)
                .collect(Collectors.toList());
        res.gameId = game.getGameId();
        gameResults.add(res);

        if (ranked) {
            calculateRating();
        }
    }

    public boolean hasGame(String gameId) {
        return gameResults.stream().anyMatch(gr -> gr.gameId.equals(gameId));
    }

    // this will redo calculations from scratch on every rating
    // could be optimixed, but is fast enough with 100000 games in under 1s
    private void calculateRating() {
        SortedMap<LocalDate, List<GameResult>> resultsByDate = new TreeMap<>(
                gameResults.stream()
                        .collect(Collectors.groupingBy(gr -> gr.date, Collectors.toList())));

        // The initial volatility is set to pretty high, to allow non competing snakes to fall rapidly in rank
        RatingCalculator ratingCalculator = new RatingCalculator(1, 0.5);

        Map<String, Rating> ratings = gameResults.stream()
                .flatMap(g -> g.positions.stream())
                .distinct()
                .collect(Collectors.toMap(name -> name, name -> new Rating(name, ratingCalculator)));

        resultsByDate
                .keySet()
                .forEach(date -> {
                    rateDay(ratingCalculator, ratings, resultsByDate.get(date));
                    log.info("Calucated ratings for "+date);
                    log.info(""+convertRating(ratings));
                });

        rating.putAll(convertRating(ratings));
    }

    private Map<String, Long> convertRating(Map<String, Rating> ratings) {
        Map<String, Long> ret = new HashMap<>();
        ratings.forEach((name,rating) ->
                ret.put(name, Math.round(rating.getRating() - 2 * rating.getRatingDeviation()))
        );
        return ret;
    }

    // Each day counts as a rating period, meaning that higher ratings get lost
    private void rateDay(RatingCalculator ratingCalculator, Map<String, Rating> ratings, List<GameResult> gameResults) {
        RatingPeriodResults dayResults = new RatingPeriodResults();

        // Add all players as participants, to get players not participating to lose rank
        ratings.values().forEach(dayResults::addParticipants);

        // Every player is considered to have a win against the player lower on the ranking list
        gameResults.forEach(gr -> {
            for (int i=0; i<gr.positions.size(); i++) {
                for (int j=i+1; j<gr.positions.size(); j++) {
                    Rating p1 = ratings.get(gr.positions.get(i));
                    Rating p2 = ratings.get(gr.positions.get(j));
                    dayResults.addResult(p1, p2);
                }
            }
        });

        ratingCalculator.updateRatings(dayResults);
    }

    // Code for testing and experimenting
    public static void main(String[] args) {
        ArenaRater testRater = new ArenaRater();

        LocalDate date = LocalDate.now();

        int days = 14;

        for (int i = 0; i<days; i++) {
            for (int j = 0; j<100; j++) {
                GameResult res = new GameResult();
                res.date = date;
                if (i < days / 2) {
                    res.positions = Arrays.asList("player1", "player2", "player3");
                } else {
                    // The leader quits
                    res.positions = Arrays.asList("player2", "player3");
                }
                testRater.gameResults.add(res);
            }

            date = date.plus(1, ChronoUnit.DAYS);
        }

        long time = System.nanoTime();
        testRater.calculateRating();
        System.err.println("Time elapsed "+(System.nanoTime() - time) / 1e9);
        System.err.println(testRater.rating);
    }

}