package se.cygni.game.random;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class XORShiftRandom extends Random {

    private AtomicLong seed = new AtomicLong(System.nanoTime());

    public XORShiftRandom() {
    }

    protected int next(int nbits) {
        // N.B. Not thread-safe!
        long x = this.seed.get();
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        this.seed.set(x);
        x &= ((1L << nbits) -1);
        return (int) x;
    }
}
