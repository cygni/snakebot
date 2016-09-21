package se.cygni.snake.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.history.GameHistory;
import se.cygni.snake.eventapi.history.GameHistorySearchItem;
import se.cygni.snake.eventapi.history.GameHistorySearchResult;

import java.util.*;

@Profile({"production"})
@Component
public class GameHistoryStorageElastic implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageElastic.class);

    private final static int MAX_SEARCH_RESULT = 20;

    @Value("${snakebot.elastic.index}")
    private String elasticIndex;

    @Value("${snakebot.elastic.type}")
    private String elasticType;


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
            IndexRequest indexRequest = new IndexRequest(elasticIndex, elasticType, gameHistory.getGameId());
            String msg = ApiMessageParser.encodeMessage(gameHistory);

            indexRequest.source(msg);
            elasticClient.index(indexRequest).actionGet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<GameHistory> getGameHistory(String gameId) {
        SearchRequestBuilder srb = elasticClient.prepareSearch(elasticIndex).setTypes(elasticType);
        SearchResponse esResponse = elasticClient.prepareSearch(elasticIndex)
                .setQuery(QueryBuilders.idsQuery(elasticType).addIds(gameId))
                .execute().actionGet();
        try {
            log.debug(esResponse.getHits().getAt(0).getSourceAsString());
            return Optional.of((GameHistory)ApiMessageParser.decodeMessage(esResponse.getHits().getAt(0).getSourceAsString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(null);
    }

    @Override
    public GameHistorySearchResult listGamesWithPlayer(String playerName) {
        SearchRequestBuilder srb = elasticClient.prepareSearch(elasticIndex).setTypes(elasticType);
        SearchResponse esResponse = elasticClient.prepareSearch(elasticIndex)
                .setQuery(QueryBuilders.matchQuery("playerNames", playerName))
                .execute().actionGet();

        List<GameHistorySearchItem> items = new ArrayList<>();

        try {
            Iterator<SearchHit> searchHitIterator = esResponse.getHits().iterator();
            int counter = 0;
            while (searchHitIterator.hasNext() && counter < MAX_SEARCH_RESULT) {
                GameHistory gh = (GameHistory)ApiMessageParser.decodeMessage(searchHitIterator.next().getSourceAsString());
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
