package se.cygni.snake.persistence.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.history.GameHistory;
import se.cygni.snake.eventapi.history.GameHistorySearchItem;
import se.cygni.snake.eventapi.history.GameHistorySearchResult;
import se.cygni.snake.persistence.history.domain.GameHistoryPersisted;

import java.util.*;

@Profile({"production"})
@Component
public class GameHistoryStorageElastic implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageElastic.class);

    private final static int MAX_SEARCH_RESULT = 20;

    @Value("${snakebot.elastic.gamehistory.index}")
    private String gameHistoryIndex;

    @Value("${snakebot.elastic.gamehistory.type}")
    private String gameHistoryType;

    @Value("${snakebot.elastic.gameevent.index}")
    private String gameEventIndex;

    @Value("${snakebot.elastic.gameevent.type}")
    private String gameEventType;

    private final EventBus eventBus;
    private final Client elasticClient;

    @Autowired
    public GameHistoryStorageElastic(EventBus eventBus, Client elasticClient) {
        log.debug("GameHistoryStorageElastic started");

        this.eventBus = eventBus;
        this.eventBus.register(this);

        this.elasticClient = elasticClient;
    }

    @Override
    @Subscribe
    public void addGameHistory(GameHistory gameHistory) {
        try {
            gameHistory.getMessages().stream().forEach( gameMessage -> {

                String eventId = UUID.randomUUID().toString();
                try {
                    IndexRequest indexRequest = new IndexRequest(gameEventIndex, gameEventType, eventId);
                    String msg = GameMessageParser.encodeMessage(gameMessage);
                    indexRequest.source(msg);
                    elasticClient.index(indexRequest).actionGet();
                } catch (Exception e) {
                    log.error("Failed to store GameEvent", e);
                }
            });

            GameHistoryPersisted ghp = new GameHistoryPersisted(
                    gameHistory.getGameId(),
                    gameHistory.getPlayerNames(),
                    gameHistory.getGameDate()
            );

            IndexRequest indexRequest = new IndexRequest(gameHistoryIndex, gameHistoryType, gameHistory.getGameId());
            String msg = ApiMessageParser.encodeMessage(ghp);
            indexRequest.source(msg);
            elasticClient.index(indexRequest).actionGet();
        } catch (Exception e) {
            log.error("Failed to store a GameHistory", e);
        }
    }

    @Override
    public Optional<GameHistory> getGameHistory(String gameId) {
        SearchResponse esResponse = elasticClient.prepareSearch(gameHistoryIndex)
                .setQuery(QueryBuilders.idsQuery(gameHistoryType).addIds(gameId))
                .execute().actionGet();
        try {
            if (esResponse.getHits().totalHits() > 0) {
                GameHistoryPersisted ghp = (GameHistoryPersisted) ApiMessageParser.decodeMessage(esResponse.getHits().getAt(0).getSourceAsString());
                List<GameMessage> gameMessages = getGameEventsForGame(gameId);

                GameHistory gameHistory = new GameHistory(
                        ghp.getGameId(),
                        ghp.getPlayerNames(),
                        ghp.getGameDate(),
                        gameMessages
                );

                return Optional.of(gameHistory);
            }
        } catch (Exception e) {
            log.error("Failed to deserialize stored GameHistory", e);
        }
        return Optional.ofNullable(null);
    }

    private List<GameMessage> getGameEventsForGame(String gameId) {
        List<GameMessage> messages = new ArrayList<>();

        QueryBuilder qb = QueryBuilders.termQuery("gameId", gameId);

        SearchResponse scrollResp = elasticClient.prepareSearch(gameEventIndex)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(200).execute().actionGet(); //200 hits per shard will be returned for each scroll

        //Scroll until no hits are returned
        while (true) {

            for (SearchHit hit : scrollResp.getHits().getHits()) {
                try {
                    GameMessage gameMessage = GameMessageParser.decodeMessage(hit.getSourceAsString());
                    messages.add(gameMessage);
                } catch (Exception e) {
                    log.error("Failed to decode GameMessage", e);
                }
            }
            scrollResp = elasticClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            //Break condition: No hits are returned
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }

        Collections.sort(messages, new Comparator<GameMessage>() {
            @Override
            public int compare(GameMessage o1, GameMessage o2) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        return messages;
    }

    @Override
    public GameHistorySearchResult listGamesWithPlayer(String playerName) {
        SearchRequestBuilder srb = elasticClient.prepareSearch(gameHistoryIndex).setTypes(gameHistoryType);
        SearchResponse esResponse = elasticClient.prepareSearch(gameHistoryIndex)
                .setQuery(QueryBuilders.matchQuery("playerNames", playerName))
                .setSize(MAX_SEARCH_RESULT)
                .execute().actionGet();

        List<GameHistorySearchItem> items = new ArrayList<>();

        try {
            Iterator<SearchHit> searchHitIterator = esResponse.getHits().iterator();
            int counter = 0;
            while (searchHitIterator.hasNext() && counter < MAX_SEARCH_RESULT) {
                GameHistoryPersisted gh = (GameHistoryPersisted)ApiMessageParser.decodeMessage(searchHitIterator.next().getSourceAsString());
                items.add(new GameHistorySearchItem(gh.getGameId(), gh.getPlayerNames(), gh.getGameDate()));
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(items, new Comparator<GameHistorySearchItem>() {
            @Override
            public int compare(GameHistorySearchItem o1, GameHistorySearchItem o2) {
                return o2.getGameDate().compareTo(o1.getGameDate());
            }
        });
        return new GameHistorySearchResult(items);
    }


}
