package com.g0atee.chance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;

public class ProbabilityTest {
	public static final double EPSILON = 0.00000001; // used for tiny differences between doubles

	Probability pImpossible = new Probability(0.0);
	Probability pHalf       = new Probability(0.5);
	Probability pCertain    = new Probability(1.0);

	@Test
	public void ConstructorsTest() {
		double tooLow  = 0.0 - Double.MIN_VALUE;
		double tooHigh = 1 + EPSILON;
		double nan     = Double.NaN;

		Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
			new Probability(tooLow);
		});
		assertEquals("Probabilities must be valid numbers between 0 and 1 included (input was "+tooLow+")", e1.getMessage());

		Exception e2 = assertThrows(IllegalArgumentException.class, () -> {
			new Probability(tooHigh);
		});
		assertEquals("Probabilities must be valid numbers between 0 and 1 included (input was "+tooHigh+")", e2.getMessage());

		Exception e3 = assertThrows(IllegalArgumentException.class, () -> {
			new Probability(nan);
		});
		assertEquals("Probabilities must be valid numbers between 0 and 1 included (input was "+nan+")", e3.getMessage());

		// Testing normal use case
		Probability p1 = new Probability(0.564);
		assertEquals(0.564, p1.value());

		// testing impossible() & certain() static factory methods
		Probability p2 = Probability.impossible();
		Probability p3 = Probability.certain();
		assertEquals(0.0, p2.value());
		assertEquals(1.0, p3.value());
	}

	@Test
	public void complementTest() {
		assertEquals(1.0, pImpossible.complement().value());
		assertEquals(0.5,       pHalf.complement().value());
		assertEquals(0.0,    pCertain.complement().value());

		// Normal use case
		Probability p1 = new Probability(0.347);
		Probability p1c = p1.complement();
		assertEquals(1 - 0.347, p1c.value());
	}

	@Test
	public void matchTest() {
		final int ITERATIONS = 100_000;

		for (int i=0; i<ITERATIONS; i++) {
			if (pImpossible.match()) {
				throw new RuntimeException("an impossible probability should never match.");
			}
		}

		for (int i=0; i<ITERATIONS; i++) {
			if (!pCertain.match()) {
				throw new RuntimeException("a certain probability should always match.");
			}
		}

		int nbMatches = 0;
		for (int i=0; i<ITERATIONS; i++) {
			if (pHalf.match()) {
				nbMatches++;
			}
		}
		double ratio = nbMatches / (double) ITERATIONS;
		assertEquals(0.5, ratio, 0.01);
	}

	@Test
	public void predicateMatchTest() {
		final int SIZE = 10_000;

		int[] randomInts = IntStream.range(0, SIZE)
			.filter(pHalf::match)
			.toArray();
		double ratio = randomInts.length / (double) SIZE;
		assertEquals(0.5, ratio, 0.01);

		int[] empty = IntStream.range(0, SIZE)
			.filter(pImpossible::match)
			.toArray();
		assertEquals(0, empty.length);

		int[] full = IntStream.range(0, SIZE)
		.filter(pCertain::match)
		.toArray();
		assertEquals(SIZE, full.length);
	}

	@Test
	public void isImpossibleTest() {
		Probability p1 = new Probability(Double.MIN_VALUE);
		assertTrue(pImpossible.isImpossible());
		assertFalse(        p1.isImpossible());
		assertFalse(     pHalf.isImpossible());
		assertFalse(  pCertain.isImpossible());
	}

	@Test
	public void isCertainTest() {
		Probability p1 = new Probability(0.999999);
		assertTrue(    pCertain.isCertain());
		assertFalse(         p1.isCertain());
		assertFalse(      pHalf.isCertain());
		assertFalse(pImpossible.isCertain());
	}

	@Test
	public void toProbabilityTest() {
		assertTrue(pImpossible == pImpossible.toProbability());
		assertTrue(pHalf       ==       pHalf.toProbability());
		assertTrue(pCertain    ==    pCertain.toProbability());
	}

	@Test
	public void toOddsTest() {
		Odds oImpossible = pImpossible.toOdds();
		Odds oHalf       = pHalf.toOdds();
		Odds oCertain    = pCertain.toOdds();
		assertTrue(oImpossible instanceof Odds);
		assertTrue(oHalf       instanceof Odds);
		assertTrue(oCertain    instanceof Odds);
		assertEquals(0.0, oImpossible.value());
		assertEquals(1.0, oHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, oCertain.value());
	}

	@Test
	public void toLogOddsTest() {
		LogOdds loImpossible = pImpossible.toLogOdds();
		LogOdds loHalf       = pHalf.toLogOdds();
		LogOdds loCertain    = pCertain.toLogOdds();
		assertTrue(loImpossible instanceof LogOdds);
		assertTrue(loHalf       instanceof LogOdds);
		assertTrue(loHalf       instanceof LogOdds);
		assertEquals(Double.NEGATIVE_INFINITY, loImpossible.value());
		assertEquals(0.0, loHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, loCertain.value());
	}

	@Test
	public void compareToTest() {
		Probability same   = new Probability(0.5);
		Probability lower  = new Probability(0.5 - EPSILON);
		Probability higher = new Probability(0.5 + EPSILON);
		assertTrue(pHalf.compareTo(same  ) == 0);
		assertTrue(pHalf.compareTo(lower ) >  0);
		assertTrue(pHalf.compareTo(higher) <  0);
		assertTrue(pHalf.compareTo(  same.toOdds()) == 0);
		assertTrue(pHalf.compareTo( lower.toOdds()) >  0);
		assertTrue(pHalf.compareTo(higher.toOdds()) <  0);
		assertTrue(pHalf.compareTo(  same.toLogOdds()) == 0);
		assertTrue(pHalf.compareTo( lower.toLogOdds()) >  0);
		assertTrue(pHalf.compareTo(higher.toLogOdds()) <  0);
	}

	@Test
	@SuppressWarnings("all") // to test equals() against unrelated type without having information dialog.
	public void equalsTest() {
		assertTrue(pImpossible.equals(new Probability(0.0)));
		assertTrue(      pHalf.equals(new Probability(0.5)));
		assertTrue(   pCertain.equals(new Probability(1.0)));
		assertFalse(pHalf.equals(new Probability(0.5 + EPSILON)));
		assertFalse(pHalf.equals(new Probability(0.5 - EPSILON)));
		assertFalse(pHalf.equals(null));
		assertFalse(pHalf.equals("not even a number"));
	}

	@Test
	public void toStringTest() {
		assertEquals("0.0%",   pImpossible.toString());
		assertEquals("15.2%",  new Probability(0.152).toString());
		assertEquals("50.0%",  pHalf.toString());
		assertEquals("73.8%",  new Probability(0.738).toString());
		assertEquals("100.0%", pCertain.toString());
	}
}
