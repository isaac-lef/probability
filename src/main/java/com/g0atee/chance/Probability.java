package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class Probability extends Chance {
	private static final long serialVersionUID = 1L;

	public static final double IMPOSSIBLE = 0.0;
	public static final double CERTAIN = 1.0;

	public Probability(final double value) {
		super(value);
		if (value < IMPOSSIBLE || value > CERTAIN || Double.isNaN(value))
			throw new IllegalArgumentException("Probabilities must be valid numbers between 0 and 1 included (input was "+value+")");
	}

	public static Probability impossible() {
		return new Probability(IMPOSSIBLE);
	}

	public static Probability certain() {
		return new Probability(CERTAIN);
	}

	/**
	 * <p>Let's say the current Probability corresponds to an event E. This returns a new Probability corresponding to the complement not(E).</p>
	 * Example : <code>p</code> is the probability of getting an <i>even</i> number when throwing a dice.<br/>
	 * then <code>p.complement()</code> is the probability of getting an <i>odd</i> number.
	 */
	public Probability complement() {
		return new Probability(1 - value);
	}

	// PREDICATES

	@Override
	public boolean match() {
		return ThreadLocalRandom.current().nextDouble() < value;
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
		return this;
	}

	@Override
	public Odds toOdds() {
		if (isCertain()) {
			return new Odds(Double.POSITIVE_INFINITY);
		}
		return new Odds(value / (1 - value));
	}

	@Override
	public LogOdds toLogOdds() {
		return toOdds().toLogOdds();
	}

	// COMPARISON

	@Override
	public int compareTo(final Chance o) {
		return Double.compare(this.value, o.toProbability().value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Chance) {
			Double val1 = Double.valueOf(this.value);
			Double val2 = Double.valueOf(((Chance) obj).toProbability().value);
			return val1.equals(val2);
		}
		return false;
	}

	@Override
	public String toString() {
		return Double.toString(value * 100) + "%";
	}
}
