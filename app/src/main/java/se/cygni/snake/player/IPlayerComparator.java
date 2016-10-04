package se.cygni.snake.player;

import se.cygni.snake.api.model.PointReason;

import java.util.Comparator;

public class IPlayerComparator implements Comparator<IPlayer> {

    private static PointReason[] POINT_REASON_PRIORITY = new PointReason [] {

            PointReason.GROWTH,
            PointReason.NIBBLE,
            PointReason.CAUSED_SNAKE_DEATH,
            PointReason.FOOD,
            PointReason.LAST_SNAKE_ALIVE
    };

    @Override
    public int compare(IPlayer o1, IPlayer o2) {
        // Note reversed order!

        // First alive or dead
        if (o2.isAlive() && !o1.isAlive())
            return 1;

        if (o1.isAlive() && !o2.isAlive())
            return -1;

        // Then points
        int pointCp = Integer.compare(o2.getTotalPoints(), o1.getTotalPoints());
        if (pointCp != 0)
            return pointCp;

        // Then point distribution
        for (PointReason reason : POINT_REASON_PRIORITY) {
            int reasonCpm = compareByPointsReason(reason, o1, o2);
            if (reasonCpm != 0)
                return reasonCpm;
        }

        // Then when died (they must both be dead if we got this far)
        return Long.compare(o2.getDiedAtTick(), o1.getDiedAtTick());

    }

    private int compareByPointsReason(PointReason reason, IPlayer o1, IPlayer o2) {
        return Integer.compare(o2.getPointsBy(reason), o1.getPointsBy(reason));
    }
}
