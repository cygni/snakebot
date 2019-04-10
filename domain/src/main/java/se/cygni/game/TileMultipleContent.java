package se.cygni.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

public class TileMultipleContent {

    private final List<WorldObject> contents;

    public TileMultipleContent() {
	contents = new ArrayList<>();
    }

    public TileMultipleContent(WorldObject content) {
	this();
	if (!(content instanceof Empty)) {
	    contents.add(content);
	}
    }

    public void addContent(WorldObject content) {
	// Empty tiles are not stored
	if (content instanceof Empty) {
	    return;
	}

	// Do not store already added contents
	if (contents.contains(content)) {
	    return;
	}

	contents.add(content);
    }

    public boolean containsExactlyOneHeadAndOneTail() {
	if (size() != 2) {
	    return false;
	}

	final WorldObject wo1 = contents.get(0);
	final WorldObject wo2 = contents.get(1);

	SnakeHead head = null;
	SnakeBody tail = null;

	if (wo1 instanceof SnakeHead) {
	    head = (SnakeHead) wo1;
	} else if (wo2 instanceof SnakeHead) {
	    head = (SnakeHead) wo2;
	}

	if (wo1 instanceof SnakeBody) {
	    final SnakeBody body = (SnakeBody) wo1;
	    if (body.isTail()) {
		tail = body;
	    }
	} else if (wo2 instanceof SnakeBody) {
	    final SnakeBody body = (SnakeBody) wo2;
	    if (body.isTail()) {
		tail = body;
	    }
	}

	if (head == null || tail == null) {
	    return false;
	}

	return true;
    }

    @SuppressWarnings({ "unchecked", "varargs" })
    public <T extends WorldObject> boolean containsExactlyOneOfEachType(Class<T>... types) {
	if (types.length != contents.size()) {
	    return false;
	}

	for (final Class<T> clazz : types) {
	    if (countInstancesOf(clazz) != 1) {
		return false;
	    }
	}
	return true;
    }

    public <T extends WorldObject> boolean containsType(Class<T> type) {
	if (!hasContent()) {
	    return false;
	}

	for (final WorldObject wo : contents) {
	    if (wo.getClass().equals(type)) {
		return true;
	    }
	}

	return false;
    }

    public <T extends WorldObject> int countInstancesOf(Class<T> type) {
	return listContentsOfType(type).size();
    }

    public WorldObject getContent() {
	if (!isValidCombinationOfContents()) {
	    throw new IllegalStateException("Invalid combination of contents in this Tile");
	}

	if (contents.isEmpty()) {
	    return new Empty();
	}

	if (contents.size() == 2 && containsType(Food.class) && containsType(SnakeHead.class)) {
	    return getFirstContentOfType(SnakeHead.class);
	}

	if (contents.size() == 1) {
	    return contents.get(0);
	}

	// ToDo: Add more cases here.
	throw new IllegalStateException("Found no way of calculating resulting content");
    }

    public List<WorldObject> getContents() {
	final List<WorldObject> contentsCopy = new ArrayList<>();
	contentsCopy.addAll(contents);
	return contentsCopy;
    }

    @SuppressWarnings("unchecked")
    private <T extends WorldObject> T getFirstContentOfType(Class<T> type) {
	for (final WorldObject wo : contents) {
	    if (wo.getClass().equals(type)) {
		return (T) wo;
	    }
	}

	throw new IllegalStateException("Could not find content of type: " + type);
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

	if (contents.size() == 2 && containsType(Food.class) && containsType(SnakeHead.class)) {
	    return true;
	}

	if (contents.size() >= 2) {
	    return false;
	}

	return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldObject> List<T> listContentsOfType(Class<T> type) {
	if (!hasContent()) {
	    return new ArrayList<>();
	}

	return contents.stream().filter(worldObject -> worldObject.getClass().equals(type)).map(typedObject -> {
	    return (T) typedObject;
	}).collect(Collectors.toList());
    }

    public List<String> listOffendingSnakeHeadIds() {
	return listContentsOfType(SnakeHead.class).stream().map(snakeHead -> snakeHead.getPlayerId())
		.collect(Collectors.toList());
    }

    public List<SnakeHead> listOffendingSnakeHeads() {
	return listContentsOfType(SnakeHead.class);
    }

    public List<String> listSnakeIdsPresent() {
	final List<String> snakeIds = new ArrayList<>();

	for (final WorldObject wo : contents) {
	    if (wo instanceof SnakePart) {
		snakeIds.add(((SnakePart) wo).getPlayerId());
	    }
	}
	return snakeIds;
    }

    public <T extends WorldObject> void removeType(Class<T> type) {
	contents.remove(getFirstContentOfType(type));
    }

    public int size() {
	return contents.size();
    }
}
