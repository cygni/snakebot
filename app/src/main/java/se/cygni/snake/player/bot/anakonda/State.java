package se.cygni.snake.player.bot.anakonda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

import se.cygni.snake.api.model.SnakeInfo;

public class State {
    public Snake[] otherSnakes;
    public Snake mySnake;
    public List<Position> obstacles;
    public final int height;
    public final int width;
    private HashMap<Position, Tile> map = new HashMap<>();

    private String status = "ONGOING";

    public final static int[][] directions = new int[][] { new int[] { 1, 0 }, // right
            new int[] { -1, 0 }, // left
            new int[] { 0, 1 }, // down
            new int[] { 0, -1 } // up
    };

    public final static String[] directionNames = new String[] { "RIGHT", "LEFT", "DOWN", "UP" };

    private boolean mapBuilt = false;

    public String status() {
        return this.status;
    }

    public State(se.cygni.snake.api.model.Map map, String myName) {
        SnakeInfo[] snakeInfos = map.getSnakeInfos();
        otherSnakes = new Snake[snakeInfos.length - 1];
        int s = 0;
        for (int i = 0; i < snakeInfos.length; i++) {
            if (Objects.equals(snakeInfos[i].getName(), myName)) {
                mySnake = new Snake(snakeInfos[i], 0, map.getWidth(), map.getHeight());
            } else {
                otherSnakes[s] = new Snake(snakeInfos[i], s + 1, map.getWidth(), map.getHeight());
                s++;
            }
        }

        obstacles = translatePositions(map.getObstaclePositions(), map.getWidth(), map.getHeight());
        width = map.getWidth();
        height = map.getHeight();
    }

    public State(Snake mySnake, Snake[] otherSnakes, List<Position> obstacles, int height, int width) {
        this.mySnake = mySnake;
        this.otherSnakes = otherSnakes;
        this.obstacles = obstacles;
        this.height = height;
        this.width = width;
    }

    public static class Tile {
        Snake occupiedBy;

        public Tile(Snake occupiedBy) {
            this.occupiedBy = occupiedBy;
        }
    }

    public static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position))
                return false;
            if (obj == this)
                return true;

            Position o = (Position) obj;
            if (o.x != this.x)
                return false;
            if (o.y != this.y)
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }

    public static class Snake {
        public LinkedList<Position> spread = new LinkedList<>();
        public final int id;

        public Snake(int id, int x, int y) {
            this.spread.addFirst(new Position(x, y));
            this.id = id;
        }

        public Snake(SnakeInfo si, int id, int width, int height) {
            this.spread = State.translatePositions(si.getPositions(), width, height);
            this.id = id;
        }

        public Snake(int id, LinkedList<Position> spread) {
            this.id = id;
            this.spread = spread;
        }

        public Position head() {
            return spread.get(0);
        }

        public boolean isDead() {
            return spread.size() == 0;
        }

        public Snake clone() {
            LinkedList<Position> spread = new LinkedList<>();
            for (Position p : this.spread) {
                spread.add(p);
            }
            return new Snake(id, spread);
        }
    }

    public static class Action {
        public Snake actor;
        public Position movesTo;
        public String dir;

        public Action(Snake actor, Position movesTo, String dir) {
            this.actor = actor;
            this.movesTo = movesTo;
            this.dir = dir;
        }

        public String toString() {
            return "[id: " + this.actor.id + ", dir: " + dir + ", x: " + this.movesTo.x + ", y: " + this.movesTo.y
                    + "]";
        }
    }

    public Set<List<Action>> myPossibleActions() {
        return possibleActions(mySnake, map);
    }

    public Set<List<Action>> othersPossibleActions() {
        return possibleActions(otherSnakes, map);
    }

    public Set<List<Action>> possibleActions(Snake snake, Map<Position, Tile> map) {
        return possibleActions(new Snake[] { snake }, map);
    }

    public Set<List<Action>> possibleActions(Snake[] snakes, Map<Position, Tile> map) {
        this.ensureMapIsBuilt();

        List<Set<Action>> allActions = new LinkedList<>();
        for (Snake snake : snakes) {
            if (snake.isDead())
                continue;
            Set<Action> actions = new HashSet<>();
            int i = 0;
            for (int[] dir : State.directions) {
                int x = snake.head().x + dir[0];
                int y = snake.head().y + dir[1];

                boolean canMove = (x > -1 && x < width) && (y > -1 && y < height)
                        && (!map.containsKey(new Position(x, y)) || (map.get(new Position(x, y)).occupiedBy != null
                                && map.get(new Position(x, y)).occupiedBy.id == 0
                                && map.get(new Position(x, y)).occupiedBy.head().equals(new Position(x, y))));

                if (canMove) {
                    actions.add(new Action(snake, new Position(x, y), State.directionNames[i]));
                }
                i++;
            }

            // Snake could not move in any direction, without dying.
            // So which direction would not make any difference.
            if (actions.size() == 0) {
                actions.add(new Action(snake,
                        new Position(snake.head().x + State.directions[0][0], snake.head().y + State.directions[0][1]),
                        State.directionNames[0]));
            }
            allActions.add(actions);
        }

        if (allActions.size() == 1) {
            Set<Action> actions = allActions.get(0);
            Set<List<Action>> r = new HashSet<>();
            for (Action a : actions) {
                List<Action> tmp = new LinkedList<>();
                tmp.add(a);
                r.add(tmp);
                tmp = new LinkedList<>();

            }
            return r;
        } else {
            return Sets.cartesianProduct(allActions);
        }

    }

    public boolean isFree(int x, int y) {
        return !this.map.containsKey(new Position(x, y));
    }

    public static State step(State state, List<Action> actions) {

        Snake mySnake = state.mySnake.clone();
        Snake[] otherSnakes = new Snake[state.otherSnakes.length];
        for (int i = 0; i < state.otherSnakes.length; i++) {
            otherSnakes[i] = state.otherSnakes[i].clone();
        }

        for (Action a : actions) {
            if (a.actor.id == 0) {
                mySnake.spread.addFirst(a.movesTo);
            } else {
                otherSnakes[a.actor.id - 1].spread.addFirst(a.movesTo);
            }
        }

        State s = new State(mySnake, otherSnakes, state.obstacles, state.height, state.width);
        s.ensureMapIsBuilt();
        return s;

    }

    static LinkedList<Position> translatePositions(int[] positions, int width, int height) {
        LinkedList<Position> p = new LinkedList<>();
        for (int pos : positions) {
            int y = pos / width;
            int x = pos - y * width;
            p.add(new Position(x, y));
        }

        return p;
    }

    public String toString() {
        this.ensureMapIsBuilt();
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile t = map.get(new Position(x, y));
                sb.append(t == null ? "-" : t.occupiedBy == null ? "*" : t.occupiedBy.id + "").append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private void ensureMapIsBuilt() {
        if (!this.mapBuilt) {

            for (Position p : obstacles) {
                this.map.put(p, new Tile(null));
            }

            for (Position p : mySnake.spread) {
                if (this.map.containsKey(p)) {
                    this.status = "LOSS";
                }
                this.map.put(p, new Tile(mySnake));
            }

            for (Snake s : otherSnakes) {
                for (Position p : s.spread) {
                    if (this.map.containsKey(p) && this.map.get(p).occupiedBy != null) {
                        if (this.map.get(p).occupiedBy.id == 0) {
                            this.status = "LOSS";
                        }
                    }
                    this.map.put(p, new Tile(s));
                }
            }
            this.mapBuilt = true;
        }
    }
}
