package se.cygni.game;

import se.cygni.game.transformation.WorldTransformation;
import se.cygni.game.worldobject.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TileMultipleContent {

    private final List<WorldObject> contents;
    private final List<WorldTransformation> resultingTransformations;

    public TileMultipleContent() {
        contents = new ArrayList<>();
        resultingTransformations = new ArrayList<>();
    }

    public TileMultipleContent(WorldObject content) {
        this();
        if (!(content instanceof Empty))
            contents.add(content);
    }

    public void addContent(WorldObject content) {
        // Empty tiles are not stored
        if (content instanceof Empty)
            return;

        // Do not store already added contents
        if (contents.contains(content)) {
            return;
        }

        contents.add(content);
    }

    public WorldObject getContent() {
        if (!isValidCombinationOfContents()) {
            throw new IllegalStateException("Invalid combination of contents in this Tile");
        }

        if (contents.isEmpty()) {
            return new Empty();
        }

        if (contents.size() == 2 &&
                containsType(Food.class) && containsType(SnakeHead.class)) {
            return getFirstContentOfType(SnakeHead.class);
        }

        if (contents.size() == 1) {
            return contents.get(0);
        }

        // ToDo: Add more cases here.
        throw new IllegalStateException("Found no way of calculating resulting content");
    }

    public List<WorldObject> getContents() {
        List<WorldObject> contentsCopy = new ArrayList<>();
        contentsCopy.addAll(contents);
        return contentsCopy;
    }

    public int size() {
        return contents.size();
    }

    public boolean hasContent() {
        return contents.size() > 0;
    }

    public boolean hasSingleContent() {
        return contents.size() == 1;
    }

    public boolean isValidCombinationOfContents() {
        if (!hasContent()) {
            return true;
        }

        if (hasSingleContent()) {
            return true;
        }

        if (contents.size() == 2 &&
                containsType(Food.class) && containsType(SnakeHead.class)) {
            return true;
        }

        // Perhaps all of the cases below can be shorted to:
        if (contents.size() >= 2) {
            return false;
        }

//        // More than one SnakeHead is never ok
//        if (countInstancesOf(SnakeHead.class) > 1) {
//            return false;
//        }
//
//        // More than one SnakeBody is never ok
//        if (countInstancesOf(SnakeBody.class) > 1) {
//            return false;
//        }
//
//        // Combination of Obstacle and SnakePart is never ok
//        if (containsType(Obstacle.class) && containsType(SnakePart.class)) {
//            return false;
//        }

        // Combination of SnakeHead and SnakeTail might be ok
//        if (containsType(SnakeHead.class) && containsType(SnakeBody.class)) {
//            return true;
//        }

        return true;
    }

    public <T extends WorldObject> boolean containsExactlyOneOfEachType(Class<T>... types) {
        if (types.length != contents.size())
            return false;

        for (Class<T> clazz : types) {
            if (countInstancesOf(clazz) != 1)
                return false;
        }
        return true;
    }

    public <T extends WorldObject> void removeType(Class<T> type) {
        contents.remove(getFirstContentOfType(type));
    }

    public List<SnakeHead> listOffendingSnakeHeads() {
        return listContentsOfType(SnakeHead.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldObject> List<T> listContentsOfType(Class<T> type) {
        if (!hasContent()) {
            return new ArrayList<>();
        }

        return contents.stream()
                .filter(worldObject -> worldObject.getClass().equals(type)).map(typedObject -> {
                    return (T) typedObject;
                })
                .collect(Collectors.toList());
    }

    public <T extends WorldObject> int countInstancesOf(Class<T> type) {
        return listContentsOfType(type).size();
    }

    public <T extends WorldObject> boolean containsType(Class<T> type) {
        if (!hasContent()) {
            return false;
        }

        for (WorldObject wo : contents) {
            if (wo.getClass().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public List<String> listSnakeIdsPresent() {
        List<String> snakeIds = new ArrayList<>();

        for (WorldObject wo : contents) {
            if (wo instanceof SnakePart) {
                snakeIds.add(((SnakePart) wo).getPlayerId());
            }
        }
        return snakeIds;
    }

    private <T extends WorldObject> T getFirstContentOfType(Class<T> type) {
        for (WorldObject wo : contents) {
            if (wo.getClass().equals(type)) {
                return (T) wo;
            }
        }

        throw new IllegalStateException("Could not find content of type: " + type);
    }
}
