package se.cygni.snake.persistence.clientinfo;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.request.ClientInfo;

@Profile({"production"})
@Component
public class ClientInfoStorageElastic implements ClientInfoStorage {

    private static Logger log = LoggerFactory
            .getLogger(ClientInfoStorageElastic.class);

    @Value("${snakebot.elastic.clientinfo.index}")
    private String elasticIndex;

    @Value("${snakebot.elastic.clientinfo.type}")
    private String elasticType;

    private final EventBus eventBus;
    private final Client elasticClient;

    public ClientInfoStorageElastic(EventBus eventBus, Client elasticClient) {
        this.eventBus = eventBus;
        this.elasticClient = elasticClient;

        this.eventBus.register(this);
    }

    @Subscribe
    @Override
    public void storeClientInfo(ClientInfo clientInfo) {
        String msg = null;
        try {
            log.debug("Storing a clientinfo");
            IndexRequest indexRequest = new IndexRequest(elasticIndex, elasticType, clientInfo.getId());
            msg = GameMessageParser.encodeMessage(clientInfo);

            indexRequest.source(msg);
            elasticClient.index(indexRequest).actionGet();
        } catch (Exception e) {
            log.error("Failed to persist clientinfo: {}", msg, e);
        }
    }
}
