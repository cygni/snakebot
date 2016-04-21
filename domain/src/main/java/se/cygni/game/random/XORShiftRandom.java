package se.cygni.game.random;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Borrowed from here:
 * http://www.javamex.com/tutorials/random_numbers/java_util_random_subclassing.shtml
 *
 */
public class XORShiftRandom extends Random {

    private AtomicLong seed = new AtomicLong(System.nanoTime());

    public XORShiftRandom() {
    }

    protected int next(int nbits) {
        long x = this.seed.get();
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        this.seed.set(x);
        x &= ((1L << nbits) -1);
        return (int) x;
    }
}
