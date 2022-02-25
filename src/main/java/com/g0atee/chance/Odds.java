package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class Odds extends Chance {
	private static final long serialVersionUID = 1L;

	public static final double IMPOSSIBLE = 0.0;
	public static final double CERTAIN = Double.POSITIVE_INFINITY;

	public Odds(final double value) {
		super(value);
		if (value < IMPOSSIBLE || Double.isNaN(value))
			throw new IllegalArgumentException("Odds must be valid numbers between 0 and +∞ included (input was "+value+")");
	}

	public static Odds impossible() {
		return new Odds(IMPOSSIBLE);
	}

	public static Odds certain() {
		return new Odds(CERTAIN);
	}

	/**
	 * <p>Let's say the current Odds corresponds to an event E. This returns a new Odds corresponding to the complement not(E).</p>
	 * Example : <code>p</code> is the odds of getting an <i>even</i> number when throwing a dice.<br/>
	 * then <code>p.complement()</code> is the odds of getting an <i>odd</i> number.
	 */
	public Odds complement() {
		if (isImpossible()) {
			return new Odds(Double.POSITIVE_INFINITY);
		}
		return new Odds(1 / value);
	}

	// PREDICATES

	@Override
	public boolean match() {
		if (isCertain()) return true;
		// as the random number generator is continuous and not inversed, we have to reduce the value to a probability
		return ThreadLocalRandom.current().nextDouble() * (value+1) < value;
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
		if (isCertain()) {
			return new Probability(1.0);
		}
		return new Probability(value / (value + 1));
	}

	@Override
	public Odds toOdds() {
		return this;
	}

	@Override
	public LogOdds toLogOdds() {
		return new LogOdds(Math.log(value));
	}

	// COMPARISON

	@Override
	public int compareTo(Chance o) {
		return Double.compare(this.value, o.toOdds().value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chance) {
			Double val1 = Double.valueOf(this.value);
			Double val2 = Double.valueOf(((Chance) obj).toOdds().value);
			return val1.equals(val2);
		}
		return false;
	}

	@Override
	public String toString() {
		if (isImpossible()) return "0/1";
		if (isCertain())    return "1/0";
		Rational r = toRational(value);
		gcdSimplify(r);
		return "" + r.num + "/" + r.denom;
	}




	// Methods to find a fraction from a double, by Scott Canady ( https://stackoverflow.com/users/4088206/scott-canady )
	// https://stackoverflow.com/a/26084714

	public class Rational {
		long num, denom;

		Rational(long num, long denom) {
			this.num = num;
			this.denom = denom;
		}
	}

	private Rational toRational(double number){
		return toRational(number, 8);
	}
		
	private Rational toRational(double number, int largestRightOfDecimal){
		long sign = 1;
		if(number < 0){
			number = -number;
			sign = -1;
		}
		
		final long SECOND_MULTIPLIER_MAX = (long)Math.pow(10, largestRightOfDecimal - 1);
		final long FIRST_MULTIPLIER_MAX = SECOND_MULTIPLIER_MAX * 10L;
		final double ERROR = Math.pow(10, -largestRightOfDecimal - 1);
		long firstMultiplier = 1;
		long secondMultiplier = 1;
		boolean notIntOrIrrational = false;
		long truncatedNumber = (long)number;
		Rational rationalNumber = new Rational((long)(sign * number * FIRST_MULTIPLIER_MAX), FIRST_MULTIPLIER_MAX);
		
		double error = number - truncatedNumber;
		while( (error >= ERROR) && (firstMultiplier <= FIRST_MULTIPLIER_MAX)){
			secondMultiplier = 1;
			firstMultiplier *= 10;
			while( (secondMultiplier <= SECOND_MULTIPLIER_MAX) && (secondMultiplier < firstMultiplier) ){
				double difference = (number * firstMultiplier) - (number * secondMultiplier);
				truncatedNumber = (long)difference;
				error = difference - truncatedNumber;
				if(error < ERROR){
					notIntOrIrrational = true;
					break;
				}
				secondMultiplier *= 10;
			}
		}
		
		if(notIntOrIrrational){
			rationalNumber = new Rational(sign * truncatedNumber, firstMultiplier - secondMultiplier);
		}
		return rationalNumber;
	}

	// My methods

	private void gcdSimplify(Rational r) {
		long gcd;
		while((gcd = gcd(r.num, r.denom)) > 1L) {
			r.num /= gcd;
			r.denom /= gcd;
		}
	}

	/**
	 * Greatest Common Divisor of two Long numbers
	 */
	private static long gcd(final long a, final long b) {
		if (a < 0 || b < 0)
			throw new IllegalArgumentException("Cannot compute greatest common divisor of negative numbers (input of "+a+" and "+b+")");
		if (a < b)
			return gcd(b, a);
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}
	
}
