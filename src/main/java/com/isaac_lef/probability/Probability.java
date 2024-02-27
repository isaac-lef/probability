package com.isaac_lef.probability;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public final class Probability implements Comparable<Probability>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final double IMPOSSIBLE = 0.0;
	public static final double CERTAIN = 1.0;
	public static final double IMPOSSIBLE_ODDS = 0.0;
	public static final double CERTAIN_ODDS = Double.POSITIVE_INFINITY;
	public static final double IMPOSSIBLE_LOGODDS = Double.NEGATIVE_INFINITY;
	public static final double CERTAIN_LOGODDS = Double.POSITIVE_INFINITY;

	private final double value;

	public Probability(final double value) {
		if (value < IMPOSSIBLE || value > CERTAIN || Double.isNaN(value))
			throw new IllegalArgumentException("Probabilities must be valid numbers between 0 and 1 included (input was "+value+")");
		this.value = value;
	}

	public static Probability fromOdds(final double odds) {
		if (odds < IMPOSSIBLE_ODDS || Double.isNaN(odds))
			throw new IllegalArgumentException("Odds must be valid numbers between 0 and +∞ included (input was "+odds+")");
		if (odds == CERTAIN_ODDS) {
			return Probability.certain();
		}
		return new Probability(odds / (odds + 1));
	}

	public static Probability fromLogOdds(final double logOdds) {
		if (Double.isNaN(logOdds))
			throw new IllegalArgumentException("Logarithmic odds must be a valid number between -∞ and +∞ included (input was "+logOdds+")");
		return fromOdds(Math.exp(logOdds));
	}

	public static Probability impossible() {
		return new Probability(IMPOSSIBLE);
	}

	public static Probability certain() {
		return new Probability(CERTAIN);
	}

	/**
	 * Access the value of the probability, a double from 0 to 1 included
	 */
	public double value() {
		return value;
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

	/**
	 * <p>This method returns <code>true</code> at random, with a chance <i>matching</i> the value it holds.</p>
	 * <p>Example:<br/>
	 * <blockquote>
	 * <code>Probability p = new Probability(0.5);
	 * if (p.match()) {
	 *     // this block has a 50% probability of being executed
	 * }
	 * </code>
	 * </blockquote>
	 * </p>
	 * <p>Uses {@link java.util.concurrent.ThreadLocalRandom#nextDouble() ThreadLocalRandom.nextDouble()}</p>
	 * <p>Not cryptographically secure!</p>
	 */
	public boolean match() {
		return ThreadLocalRandom.current().nextDouble() < value;
	}

	/**
	 * <p>A {@link java.util.function.Predicate Predicate} that takes whatever object/value, and returns the result of {@link #match()}.</p>
	 * <p>For use in {@link java.util.stream.Stream#filter(java.util.function.Predicate) Stream.filter()} :</p>
	 * <blockquote>
	 * <code>
	 * Probability p = new Probability(0.5);
	 * int[] randomInts = IntStream.range(0, 1000)
	 * 	.filter(p::match)
	 * 	.toArray();
	 * </code>
	 * </blockquote>
	 */
	public <T> boolean match(T t) {
		return match();
	}

	public boolean isImpossible() {
		return value == IMPOSSIBLE;
	}

	public boolean isCertain() {
		return value == CERTAIN;
	}

	// CASTING TO OTHER TYPES

	public double toOdds() {
		if (isCertain()) {
			return Double.POSITIVE_INFINITY;
		}
		return value / (1 - value);
	}

	public double toLogOdds() {
		return Math.log(toOdds());
	}

	// COMPARISON

	@Override
	public int compareTo(final Probability probability) {
		return Double.compare(this.value, probability.value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Probability) {
			Double val1 = Double.valueOf(this.value);
			Double val2 = Double.valueOf(((Probability) obj).value);
			return val1.equals(val2);
		}
		return false;
	}

	@Override
	public String toString() {
		return Double.toString(value * 100) + "%";
	}
}
