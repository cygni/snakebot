package se.cygni.snake.history.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameHistoryRepository extends MongoRepository<GameHistory, String> {

    public GameHistory findByGameId(String id);
}
