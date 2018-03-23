package se.cygni.snake.player.bot.anakonda.maxmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import se.cygni.snake.player.bot.anakonda.State;

/**
 * Created by ericwenn on 2017-11-01.
 */
public class MaxMin {

    private static class WorkResult {
        private final String dir;
        private final Integer value;

        private WorkResult(String dir, Integer value) {
            this.dir = dir;
            this.value = value;
        }
    }

    private static class MakeWork implements Callable<WorkResult> {

        private final State state;
        private final int depth;
        private final List<State.Action> action;
        private final String dir;
        private int utility_look;

        public MakeWork(State state, int depth, List<State.Action> action, int utility_look) {
            this.state = state;
            this.depth = depth;
            this.action = action;
            this.dir = action.get(0).dir;
            this.utility_look = utility_look;
        }

        @Override
        public WorkResult call() throws Exception {
            int value = step_maxmin(state, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE, action, utility_look);
            return new WorkResult(dir, value);
        }
    }

    public static ExecutorService es = Executors.newFixedThreadPool(3);

    public static String bestDirection2(State state, int depth, int utility_look) {
        Set<List<State.Action>> actions = state.myPossibleActions();
        List<Callable<WorkResult>> work = new ArrayList<>(actions.size());
        String defaultDirIfInterupted = null;
        for (List<State.Action> act : actions) {
            if (defaultDirIfInterupted == null) {
                defaultDirIfInterupted = act.get(0).dir;
            }
            work.add(new MakeWork(state, depth - 1, act, utility_look));
        }

        try {
            List<Future<WorkResult>> futures = es.invokeAll(work);
            int maxval = -Integer.MAX_VALUE;
            String maxdir = "";
            for (Future<WorkResult> fwr : futures) {
                WorkResult wr = fwr.get();
                if (wr.value > maxval) {
                    maxval = wr.value;
                    maxdir = wr.dir;
                }
            }

            return maxdir;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return defaultDirIfInterupted;
        }
    }

    public static String bestDirection(State state, int depth, int utility_look) {

        int maxval = -Integer.MAX_VALUE;
        String maxDir = "";
        Set<List<State.Action>> actions = state.myPossibleActions();
        for (List<State.Action> act : actions) {
            int val = step_maxmin(state, depth - 1, -Integer.MAX_VALUE, Integer.MAX_VALUE, act, utility_look);
            if (val > maxval) {
                maxval = val;
                maxDir = act.get(0).dir;
            }
        }
        return maxDir;
    }

    private static int maxmin(State state, int depth, int alpha, int beta, int utility_look) {

        if (Objects.equals(state.status(), "WIN")) {
            return 10000 * (depth + 1);
        }

        if (Objects.equals(state.status(), "LOSS")) {
            return -10000 * (depth + 1);
        }

        if (depth == 0) {
            int ut = utility(state, utility_look);
            //            System.out.println(state);
            //            System.out.println(ut);

            return ut;
        }

        // maximizing
        if (depth % 2 == 0) {
            int v = Integer.MIN_VALUE;
            Set<List<State.Action>> actions = state.myPossibleActions();
            for (List<State.Action> action : actions) {
                v = Integer.max(v, step_maxmin(state, depth - 1, alpha, beta, action, utility_look));
                alpha = Integer.max(alpha, v);
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        } else {
            int v = Integer.MAX_VALUE;
            Set<List<State.Action>> actions = state.othersPossibleActions();
            for (List<State.Action> action : actions) {
                v = Integer.min(v, step_maxmin(state, depth - 1, alpha, beta, action, utility_look));
                beta = Integer.min(beta, v);
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        }
    }

    /**
     * Recieves a list of actions and transforms the state according to the actions.
     * Then passes through to maxmin for further evaluation of tree.
     * @param state
     * @param actions
     */
    private static int step_maxmin(State state, int depth, int alpha, int beta, List<State.Action> actions,
            int utility_look) {
        State s = State.step(state, actions);
        if (Objects.equals(s.status(), "KILL")) {
            System.out.println("KILL " + depth);
            return 9999 * (15 - depth);
        }
        return maxmin(s, depth, alpha, beta, utility_look);
    }

    private static class UtilityMarker {
        private int[] markedBy;
        private int size;

        UtilityMarker(int id, int numSnakes) {
            this.markedBy = new int[numSnakes * 2];
            this.markedBy[0] = id;
            this.size = 1;
        }

        public void mark(int id) {
            this.markedBy[size] = id;
            this.size++;
        }

        public String toString() {
            return "[" + size + ", " + Arrays.toString(markedBy) + "]";
        }
    }

    private static class UtilityQueueItem {
        private final int id;
        private final int x;
        private final int y;

        UtilityQueueItem(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UtilityQueueItem))
                return false;
            if (obj == this)
                return true;

            UtilityQueueItem o = (UtilityQueueItem) obj;
            if (o.id != this.id)
                return false;
            if (o.x != this.x)
                return false;
            if (o.y != this.y)
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, x, y);
        }

        public String toString() {
            return "[" + id + ", " + (1 + x) + ", " + (1 + y) + "]";
        }
    }

    static int utility(State state, int utility_look) {

        int height = state.height;
        int width = state.width;

        int numSnakes = state.otherSnakes.length + 1;
        Queue<UtilityQueueItem> Q = new LinkedList<>();
        Queue<UtilityQueueItem> P = new LinkedList<>();
        UtilityMarker[][] V = new UtilityMarker[width][height];
        int[] scores = new int[numSnakes];
        int[] tilesChecked = new int[numSnakes + 1];

        Q.add(new UtilityQueueItem(state.mySnake.id, state.mySnake.head().x, state.mySnake.head().y));
        for (State.Snake snake : state.otherSnakes) {
            if (snake.isDead() || snake.head().x > width - 1 || snake.head().x < 0 || snake.head().y > width - 1
                    || snake.head().y < 0)
                continue;
            Q.add(new UtilityQueueItem(snake.id, snake.head().x, snake.head().y));
        }

        while (!Q.isEmpty()) {
            while (!Q.isEmpty()) {
                UtilityQueueItem item = Q.poll();
                if (item.x > width - 1 || item.x < 0 || item.y > width - 1 || item.y < 0) {

                } else if (V[item.x][item.y] == null) {
                    // This node has not been visited yet
                    V[item.x][item.y] = new UtilityMarker(item.id, numSnakes);
                    if (item.id != -1) {
                        scores[item.id]++;
                    }
                    P.add(item);
                } else {
                    // This node has been visited at least once
                    if (V[item.x][item.y].size == 1) {
                        // This node has been visited once only, but the visitor still got points for it.
                        int id = V[item.x][item.y].markedBy[0];
                        if (id != -1) {
                            scores[id]--;
                        }

                    }
                    UtilityMarker um = V[item.x][item.y];
                    um.mark(item.id);

                }
            }
            Set<UtilityQueueItem> actions = new HashSet<>();
            while (!P.isEmpty()) {
                UtilityQueueItem item = P.poll();

                // If more than one visited the same node, merge them with a "shadow" id.
                int id = (V[item.x][item.y].size != 1) ? -1 : item.id;

                // Put new possible nodes in Q
                for (int[] dir : State.directions) {
                    int x = item.x + dir[0];
                    int y = item.y + dir[1];
                    boolean isPossibleNode = (tilesChecked[item.id + 1] < utility_look) && (x > -1 && x < width)
                            && (y > -1 && y < height) && (V[x][y] == null) && state.isFree(x, y);

                    UtilityQueueItem node = new UtilityQueueItem(id, x, y);
                    if (isPossibleNode && !actions.contains(node)) {
                        tilesChecked[item.id + 1]++;
                        Q.add(node);
                        actions.add(node);
                    }
                }
            }
        }

        int utility = scores[state.mySnake.id] - 1;
        for (State.Snake snake : state.otherSnakes) {
            utility -= (scores[snake.id] - 1);
        }

        return utility;

    }

}
