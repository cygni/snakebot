package se.cygni.snake.apiconversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.model.PlayerRank;
import se.cygni.snake.game.GameResult;
import se.cygni.snake.player.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class GameResultConverter {

    private static Logger log = LoggerFactory
            .getLogger(GameResultConverter.class);

    public static List<PlayerRank> getPlayerRanks(GameResult gameResult) {
        List<IPlayer> players = gameResult.getSortedResult();

        List<PlayerRank> playerRanks = new ArrayList<>();
        int c = 1;
        for (IPlayer player : players) {
            PlayerRank pr = new PlayerRank(player.getName(), player.getPlayerId(), c, player.getTotalPoints(), player.isAlive());
            playerRanks.add(pr);
            c++;
        }

        return playerRanks;
    }
}
