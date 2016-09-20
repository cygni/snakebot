package se.cygni.snake.history.repository;

import java.util.List;

public class GameHistorySearchResult {

    private List<GameHistorySearchItem> items;

    public GameHistorySearchResult() {
    }

    public GameHistorySearchResult(List<GameHistorySearchItem> items) {
        this.items = items;
    }

    public List<GameHistorySearchItem> getItems() {
        return items;
    }

    public void setItems(List<GameHistorySearchItem> items) {
        this.items = items;
    }
}
