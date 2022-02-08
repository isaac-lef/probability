package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class Probability implements Chance {
	private final double value;

	public Probability(final double value) {
		if (value < 0.0 || value > 1.0 || Double.isNaN(value))
			throw new IllegalArgumentException("Probabilities must be valid numbers between 0 and 1 included (input was "+value+")");

		this.value = value;
	}

	@Override
	public double value() {
		return value;
	}

	@Override
	public Chance complement() {
		return new Probability(1.0 - value);
	}

	// PREDICATES

	@Override
	public boolean match() {
		return ThreadLocalRandom.current().nextDouble() < value;
	}

	@Override
	public boolean isImpossible() {
		return value == 0.0;
	}

	@Override
	public boolean isCertain() {
		return value == 1.0;
	}

	// CASTING TO OTHER TYPES

	public Odds toOdds() {
		if (isCertain()) {
			return new Odds(Double.POSITIVE_INFINITY);
		}
		return new Odds(value / (1 - value));
	}

	public LogOdds toLogOdds() {
		return toOdds().toLogOdds();
	}

	// COMPARISON

	@Override
	public int compareTo(final Chance o) {
		if (o instanceof Probability) {
			return Double.compare(this.value, ((Probability) o).value);
		}
		// TODO : compareTo other types
		return 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Probability) {
			return Double.valueOf(this.value).equals( Double.valueOf(((Probability) obj).value ));
		}
		// TODO : equals on other types
		return false;
	}

	@Override
	public String toString() {
		return Double.toString(value * 100) + "%";
	}
}
