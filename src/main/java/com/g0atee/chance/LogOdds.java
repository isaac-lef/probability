package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public class LogOdds implements Chance {
    private final double value;

    public LogOdds(double value) {
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Logarithmic odds should be a valid number between -∞ and +∞ included, not " + value + ".");

        this.value = value;
    }

    // TODO : cast to other types

    @Override
    public int compareTo(Chance o) {
        if (o instanceof LogOdds) {
            return Double.compare(this.value, ((LogOdds) o).value);
        }
        // TODO : compareTo other types
        return 0;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public Chance complement() {
        return new LogOdds(-value);
    }

    @Override
    public boolean match() {
        return ThreadLocalRandom.current().nextDouble(-Double.MAX_VALUE, Double.MAX_VALUE) < value;
    }

    @Override
    public boolean isImpossible() {
        return value == Double.NEGATIVE_INFINITY;
    }

    @Override
    public boolean isCertain() {
        return value == Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LogOdds) {
            return Double.valueOf(this.value).equals( Double.valueOf(((LogOdds) obj).value ));
        }
        // TODO : equals other types
        return false;
    }
    
}
