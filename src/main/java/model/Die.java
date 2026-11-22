package model;

import java.util.Random;

public class Die {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 6;

    private final Random random;
    private int lastValue;

    public Die() {
        this.random = new Random();
        this.lastValue = 0;
    }

    public Die(long seed) {
        this.random = new Random(seed);
        this.lastValue = 0;
    }

    public int roll() {
        lastValue = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
        return lastValue;
    }

    public int getLastValue() {
        return lastValue;
    }

    public boolean grantsExtraTurn() {
        return lastValue == 6;
    }
}