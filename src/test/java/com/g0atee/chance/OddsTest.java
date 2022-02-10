package com.g0atee.chance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public final class OddsTest {
	private OddsTest(){} // Constructor is private so that the class cannot be instanciated

	private static final double EPSILON = 0.000_000_000_000_001; // used for tiny differences between doubles

	private static final Odds oImpossible = new Odds(0.0);
	private static final Odds oHalf       = new Odds(1.0);
	private static final Odds oCertain    = new Odds(Double.POSITIVE_INFINITY);

	@Test
	void constructors() {
		double tooLow = 0.0 - Double.MIN_VALUE;
		double nan = Double.NaN;

		Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
			new Odds(tooLow);
		});
		assertEquals("Odds must be valid numbers between 0 and +∞ included (input was "+tooLow+")", e1.getMessage());

		Exception e2 = assertThrows(IllegalArgumentException.class, () -> {
			new Odds(nan);
		});
		assertEquals("Odds must be valid numbers between 0 and +∞ included (input was "+nan+")", e2.getMessage());

		// Testing normal use case
		Odds o1 = new Odds(0.564);
		assertEquals(0.564, o1.value());

		// testing impossible() & certain() static factory methods
		Odds o2 = Odds.impossible();
		Odds o3 = Odds.certain();
		assertEquals(0.0, o2.value());
		assertEquals(Double.POSITIVE_INFINITY, o3.value());
	}

	@Test
	void complement() {
		assertEquals(Double.POSITIVE_INFINITY, oImpossible.complement().value());
		assertEquals(1.0,    oHalf.complement().value());
		assertEquals(0.0, oCertain.complement().value());

		// Normal use case
		Odds o1 = new Odds(34.7);
		Odds o1c = o1.complement();
		assertEquals(1 / 34.7, o1c.value());
	}

	@Test
	void booleanSupplierMatch() {
		final int ITERATIONS = 100_000;

		for (int i=0; i<ITERATIONS; i++)
			if (oImpossible.match())
				throw new RuntimeException("impossible odds should never match.");

		for (int i=0; i<ITERATIONS; i++)
			if (!oCertain.match())
				throw new RuntimeException("certain odds should always match.");

		int nbMatches = 0;
		for (int i=0; i<ITERATIONS; i++)
			if (oHalf.match())
				nbMatches++;

		double ratio = nbMatches / (double) ITERATIONS;
		assertEquals(0.5, ratio, 0.01);
	}

	@Test
	void predicateMatch() {
		final int SIZE = 10_000;

		int[] randomInts = IntStream.range(0, SIZE)
			.filter(oHalf::match)
			.toArray();
		double ratio = randomInts.length / (double) SIZE;
		assertEquals(0.5, ratio, 0.01);

		int[] empty = IntStream.range(0, SIZE)
			.filter(oImpossible::match)
			.toArray();
		assertEquals(0, empty.length);

		int[] full = IntStream.range(0, SIZE)
		.filter(oCertain::match)
		.toArray();
		assertEquals(SIZE, full.length);
	}
}
