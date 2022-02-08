package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class LogOdds implements Chance {
	private final double value;

	public LogOdds(double value) {
		if (Double.isNaN(value))
			throw new IllegalArgumentException("Logarithmic odds should be a valid number between -∞ and +∞ included, not " + value + ".");

		this.value = value;
	}

	@Override
	public double value() {
		return value;
	}

	@Override
	public Chance complement() {
		return new LogOdds(-value);
	}

	// PREDICATES

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

	// CASTING TO OTHER TYPES

	@Override
	public Probability toProbability() {
		return toOdds().toProbability();
	}

	@Override
	public Odds toOdds() {
		return new Odds(Math.exp(value));
	}

	@Override
	public LogOdds toLogOdds() {
		return this;
	}

	// COMPARISON

	@Override
	public int compareTo(Chance o) {
		return Double.compare(this.value, o.toLogOdds().value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chance) {
			Double val1 = Double.valueOf(this.value);
			Double val2 = Double.valueOf(((Chance) obj).toLogOdds().value);
			return val1.equals(val2);
		}
		return false;
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}
	
}
