package com.g0atee.chance;

public abstract class Chance implements Comparable<Chance>{
	protected final double value;

	protected Chance(double value) {
		this.value = value;
	}

	/**
	 * Access the value stored in this Chance
	 */
	public double value() {
		return value;
	}

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
	public abstract boolean match();

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

	/**
	 * Check if the value is the lowest acceptable one, a.k.a if this chance corresponds to an impossible event.
	 */
	public abstract boolean isImpossible();

	/**
	 * Check if the value is the highest acceptable one, a.k.a if this chance corresponds to an event that happens systematically.
	 */
	public abstract boolean isCertain();

	public abstract Probability toProbability();

	public abstract Odds toOdds();

	public abstract LogOdds toLogOdds();

	/**
	 * Natural order of Chances, from least probable to most probable.
	 */
	public abstract int compareTo(Chance c);
}
