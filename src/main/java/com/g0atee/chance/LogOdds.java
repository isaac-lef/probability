package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class LogOdds extends Chance {
	public static final double IMPOSSIBLE = Double.NEGATIVE_INFINITY;
	public static final double CERTAIN = Double.POSITIVE_INFINITY;

	public LogOdds(double value) {
		super(value);
		if (Double.isNaN(value))
			throw new IllegalArgumentException("Logarithmic odds should be a valid number between -∞ and +∞ included, not " + value + ".");
	}

	public static LogOdds impossible() {
		return new LogOdds(IMPOSSIBLE);
	}

	public static LogOdds certain() {
		return new LogOdds(CERTAIN);
	}

	/**
	 * <p>Let's say the current LogOdds corresponds to an event E. This returns a new LogOdds corresponding to the complement not(E).</p>
	 * Example : <code>p</code> is the LogOdds of getting an <i>even</i> number when throwing a dice.<br/>
	 * then <code>p.complement()</code> is the LogOdds of getting an <i>odd</i> number.
	 */
	public LogOdds complement() {
		return new LogOdds(-value);
	}

	// PREDICATES

	@Override
	public boolean match() {
		return ThreadLocalRandom.current().nextDouble(-Double.MAX_VALUE, Double.MAX_VALUE) < value;
	}

	@Override
	public boolean isImpossible() {
		return value == IMPOSSIBLE;
	}

	@Override
	public boolean isCertain() {
		return value == CERTAIN;
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
