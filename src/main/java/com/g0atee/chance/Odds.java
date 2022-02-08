package com.g0atee.chance;

import java.util.concurrent.ThreadLocalRandom;

public final class Odds implements Chance {
	private final double value;

	public Odds(final double value) {
		if (value < 0.0 || Double.isNaN(value))
			throw new IllegalArgumentException("Odds must be valid numbers between 0 and +∞ included");

		this.value = value;
	}

	@Override
	public double value() {
		return value;
	}

	@Override
	public Chance complement() {
		if (value == 0.0) {
			return new Odds(Double.POSITIVE_INFINITY);
		}
		// TODO : verify if the case value=POSITIVE_INFINITY is correctly handled
		return new Odds(1 / value);
	}

	// PREDICATES

	@Override
	public boolean match() {
		return ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE) < value;
	}

	@Override
	public boolean isImpossible() {
		return value == 0.0;
	}

	@Override
	public boolean isCertain() {
		return value == Double.POSITIVE_INFINITY;
	}

	// CASTING TO OTHER TYPES

	public Probability toProbability() {
		if (isCertain()) {
			return new Probability(1.0);
		}
		return new Probability(value / (value + 1));
	}

	public LogOdds toLogOdds() {
		return new LogOdds(Math.log(value));
	}

	// COMPARISON

	@Override
	public int compareTo(Chance o) {
		if (o instanceof Odds) {
			return Double.compare(this.value, ((Odds) o).value);
		}
		// TODO : compareTo other types
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Odds) {
			return Double.valueOf(this.value).equals( Double.valueOf(((Odds) obj).value ));
		}
		// TODO : equals on other types
		return false;
	}

	@Override
	public String toString() {
		Rational r = toRational(value);
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
	
}
