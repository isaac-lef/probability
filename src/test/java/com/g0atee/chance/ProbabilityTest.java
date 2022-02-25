package com.g0atee.chance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.stream.IntStream;

public final class ProbabilityTest {
	private ProbabilityTest(){} // Constructor is private so that the class cannot be instanciated

	private static final double EPSILON = 0.000_000_000_000_001; // used for tiny differences between doubles

	private static final Probability pImpossible = new Probability(0.0);
	private static final Probability pHalf       = new Probability(0.5);
	private static final Probability pCertain    = new Probability(1.0);

	@Test
	void constructors() {
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
	void complement() {
		assertEquals(1.0, pImpossible.complement().value());
		assertEquals(0.5,       pHalf.complement().value());
		assertEquals(0.0,    pCertain.complement().value());

		// Normal use case
		Probability p1 = new Probability(0.347);
		Probability p1c = p1.complement();
		assertEquals(1 - 0.347, p1c.value());
	}

	@Test
	void booleanSupplierMatch() {
		final int ITERATIONS = 100_000;

		for (int i=0; i<ITERATIONS; i++)
			if (pImpossible.match())
				throw new RuntimeException("an impossible probability should never match.");

		for (int i=0; i<ITERATIONS; i++)
			if (!pCertain.match())
				throw new RuntimeException("a certain probability should always match.");

		int nbMatches = 0;
		for (int i=0; i<ITERATIONS; i++)
			if (pHalf.match())
				nbMatches++;

		double ratio = nbMatches / (double) ITERATIONS;
		assertEquals(0.5, ratio, 0.01);
	}

	@Test
	void predicateMatch() {
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
	void isImpossible() {
		Probability p1 = new Probability(Double.MIN_VALUE);
		assertTrue(pImpossible.isImpossible());
		assertFalse(        p1.isImpossible());
		assertFalse(     pHalf.isImpossible());
		assertFalse(  pCertain.isImpossible());
	}

	@Test
	void isCertain() {
		Probability p1 = new Probability(1 - EPSILON);
		assertTrue(    pCertain.isCertain());
		assertFalse(         p1.isCertain());
		assertFalse(      pHalf.isCertain());
		assertFalse(pImpossible.isCertain());
	}

	@Test
	void toProbability() {
		assertTrue(pImpossible == pImpossible.toProbability());
		assertTrue(pHalf       == pHalf.toProbability());
		assertTrue(pCertain    == pCertain.toProbability());
	}

	@Test
	void toOdds() {
		Odds oImpossible = pImpossible.toOdds();
		Odds oHalf       = pHalf.toOdds();
		Odds oCertain    = pCertain.toOdds();
		assertEquals(0.0, oImpossible.value());
		assertEquals(1.0, oHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, oCertain.value());
	}

	@Test
	void toLogOdds() {
		LogOdds loImpossible = pImpossible.toLogOdds();
		LogOdds loHalf       = pHalf.toLogOdds();
		LogOdds loCertain    = pCertain.toLogOdds();
		assertEquals(Double.NEGATIVE_INFINITY, loImpossible.value());
		assertEquals(0.0, loHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, loCertain.value());
	}

	@Test
	void compareTo() {
		Probability same   = new Probability(0.5);
		Probability lower  = new Probability(0.5 - EPSILON);
		Probability higher = new Probability(0.5 + EPSILON);
		assertTrue(pHalf.compareTo(  same) == 0);
		assertTrue(pHalf.compareTo( lower) >  0);
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
	void equals() {
		assertTrue(pImpossible.equals(new Probability(0.0)));
		assertTrue(      pHalf.equals(new Probability(0.5)));
		assertTrue(   pCertain.equals(new Probability(1.0)));
		assertFalse(pHalf.equals(new Probability(0.5 + EPSILON)));
		assertFalse(pHalf.equals(new Probability(0.5 - EPSILON)));
		assertFalse(pHalf.equals(null));
		assertFalse(pHalf.equals("not even a number"));
	}

	@Test
	void ProbabilitytoString() {
		assertEquals("0.0%",   pImpossible.toString());
		assertEquals("15.2%",  new Probability(0.152).toString());
		assertEquals("50.0%",  pHalf.toString());
		assertEquals("73.8%",  new Probability(0.738).toString());
		assertEquals("100.0%", pCertain.toString());
	}

	@Test
	void serialization() throws IOException, ClassNotFoundException {
		String fileName = "probability serialization.txt";
		Probability pOut = new Probability(0.564);

		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(pOut);
		objectOutputStream.flush();
		objectOutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Probability pIn = (Probability) objectInputStream.readObject();
		objectInputStream.close();

		assertTrue(pOut.value() == pIn.value());

		// deleting created file
		File f = new File(fileName);
		f.delete();
	}
}
