package se.cygni.snake.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;

@Profile({"production"})
@Configuration
public class ElasticConfig {

    @Value("${snakebot.elastic.host}")
    private String elasticHost;

    @Value("${snakebot.elastic.port}")
    private int elasticPort;

    @Bean
    public Client elasticSearchClient() throws Exception {
        return TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), elasticPort));
    }
}