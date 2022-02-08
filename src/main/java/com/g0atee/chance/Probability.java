package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public class Probability implements Chance {
	private final double value;

	public Probability(double value) {
		if (value < 0.0 || value > 1.0)
			throw new IllegalArgumentException("Probabilities must be between 0 and 1 (input was "+value+")");

		this.value = value;
	}

	@Override
	public double value() {
		return value;
	}

    @Override
    public boolean isImpossible() {
        return value == 0.0;
    }

    @Override
    public int compareTo(Chance o) {
        if (o instanceof Probability) {
            return Double.compare(this.value, ((Probability) o).value);
        }
        // TODO : other types
        return 0;
    }

    @Override
    public Chance complement() {
        return new Probability(1.0 - value);
    }

    @Override
    public boolean match() {
        return ThreadLocalRandom.current().nextDouble() < value;
    }

    @Override
    public boolean isCertain() {
        return value == 1.0;
    }

    @Override
    public String toString() {
        return Double.toString(value * 100) + "%";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Probability) {
            return this.value == ((Probability) obj).value;
        }
        // TODO : other types
        return false;
    }
}
