package com.g0atee.chance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	void isImpossible() {
		Odds o1 = new Odds(Double.MIN_VALUE);
		assertTrue(oImpossible.isImpossible());
		assertFalse(        o1.isImpossible());
		assertFalse(     oHalf.isImpossible());
		assertFalse(  oCertain.isImpossible());
	}

	@Test
	void isCertain() {
		Odds o1 = new Odds(Double.MAX_VALUE);
		assertTrue(    oCertain.isCertain());
		assertFalse(         o1.isCertain());
		assertFalse(      oHalf.isCertain());
		assertFalse(oImpossible.isCertain());
	}

	@Test
	void toProbability() {
		Probability pImpossible = oImpossible.toProbability();
		Probability pHalf       = oHalf.toProbability();
		Probability pCertain    = oCertain.toProbability();
		assertEquals(0.0, pImpossible.value());
		assertEquals(0.5, pHalf.value());
		assertEquals(1.0, pCertain.value());
	}

	@Test
	void toOdds() {
		assertTrue(oImpossible == oImpossible.toOdds());
		assertTrue(oHalf       == oHalf.toOdds());
		assertTrue(oCertain    == oCertain.toOdds());
	}

	@Test
	void toLogOdds() {
		LogOdds loImpossible = oImpossible.toLogOdds();
		LogOdds loHalf       = oHalf.toLogOdds();
		LogOdds loCertain    = oCertain.toLogOdds();
		assertEquals(Double.NEGATIVE_INFINITY, loImpossible.value());
		assertEquals(0.0, loHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, loCertain.value());
	}

	@Test
	void compareTo() {
		Odds same   = new Odds(1.0);
		Odds lower  = new Odds(1.0 - EPSILON);
		Odds higher = new Odds(1.0 + EPSILON);
		assertTrue(oHalf.compareTo(  same.toProbability()) == 0);
		assertTrue(oHalf.compareTo( lower.toProbability()) >  0);
		assertTrue(oHalf.compareTo(higher.toProbability()) <  0);
		assertTrue(oHalf.compareTo(  same) == 0);
		assertTrue(oHalf.compareTo( lower) >  0);
		assertTrue(oHalf.compareTo(higher) <  0);
		assertTrue(oHalf.compareTo(  same.toLogOdds()) == 0);
		assertTrue(oHalf.compareTo( lower.toLogOdds()) >  0);
		assertTrue(oHalf.compareTo(higher.toLogOdds()) <  0);
	}

	@Test
	@SuppressWarnings("all") // to test equals() against unrelated type without having information dialog.
	void equals() {
		assertTrue(oImpossible.equals(new Odds(0.0)));
		assertTrue(      oHalf.equals(new Odds(1.0)));
		assertTrue(   oCertain.equals(new Odds(Double.POSITIVE_INFINITY)));
		assertFalse(oHalf.equals(new Odds(1.0 + EPSILON)));
		assertFalse(oHalf.equals(new Odds(1.0 - EPSILON)));
		assertFalse(oHalf.equals(null));
		assertFalse(oHalf.equals("not even a number"));
	}

	@Test
	void oddsToString() {
		assertEquals("0/1", oImpossible.toString());
		assertEquals("4/11", new Odds(4/11.0).toString());
		assertEquals("1/1", oHalf.toString());
		assertEquals("13/5", new Odds(13/5.0).toString());
		assertEquals("1/0", oCertain.toString());
	}
}
