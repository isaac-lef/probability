package com.g0atee.chance;

public interface Chance extends Comparable<Chance>{
	/**
	 * Access the value stored in this Chance
	 */
	public double value();

	/**
	 * <p>Let's say the current Chance corresponds to an event E. This returns a new Chance corresponding to the complement not(E).</p>
	 * Example : <code>p</code> is the chance of getting an <i>even</i> number when throwing a dice.<br/>
	 * then <code>p.complement()</code> is the chance of getting an <i>odd</i> number.
	 */
	public Chance complement();

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
	public boolean match();

	/**
	 * Check if the value is the lowest acceptable one, a.k.a if this chance corresponds to an impossible event.
	 */
	public boolean isImpossible();

	/**
	 * Check if the value is the highest acceptable one, a.k.a if this chance corresponds to an event that happens systematically.
	 */
	public boolean isCertain();

	public Probability toProbability();

	public Odds toOdds();

	public LogOdds toLogOdds();
}
