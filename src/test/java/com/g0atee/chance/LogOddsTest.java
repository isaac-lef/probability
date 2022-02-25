package com.g0atee.chance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public final class LogOddsTest {
	private LogOddsTest(){} // Constructor is private so that the class cannot be instanciated

	private static final double EPSILON = 0.000_000_000_000_001; // used for tiny differences between doubles

	private static final LogOdds loImpossible = new LogOdds(Double.NEGATIVE_INFINITY);
	private static final LogOdds loHalf       = new LogOdds(0.0);
	private static final LogOdds loCertain    = new LogOdds(Double.POSITIVE_INFINITY);

	@Test
	void constructors() {
		double nan = Double.NaN;

		Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
			new LogOdds(nan);
		});
		assertEquals("Logarithmic odds must be a valid number between -∞ and +∞ included (input was "+nan+")", e1.getMessage());

		// Testing normal use case
		LogOdds lo1 = new LogOdds(-5.564);
		assertEquals(-5.564, lo1.value());

		// testing impossible() & certain() static factory methods
		LogOdds lo2 = LogOdds.impossible();
		LogOdds lo3 = LogOdds.certain();
		assertEquals(Double.NEGATIVE_INFINITY, lo2.value());
		assertEquals(Double.POSITIVE_INFINITY, lo3.value());
	}

	@Test
	void complement() {
		assertEquals(Double.POSITIVE_INFINITY, loImpossible.complement().value());
		assertEquals(Double.NEGATIVE_INFINITY, loCertain.complement().value());
		assertTrue(0.0 == loHalf.complement().value()); // assertEquals would consider +0.0 and -0.0 as different values

		// Normal use case
		LogOdds lo1 = new LogOdds(-34.7);
		LogOdds lo1c = lo1.complement();
		assertEquals(+34.7, lo1c.value());
	}

	@Test
	void booleanSupplierMatch() {
		final int ITERATIONS = 100_000;

		for (int i=0; i<ITERATIONS; i++)
			if (loImpossible.match())
				throw new RuntimeException("impossible odds should never match.");

		for (int i=0; i<ITERATIONS; i++)
			if (!loCertain.match())
				throw new RuntimeException("certain odds should always match.");

		int nbMatches = 0;
		for (int i=0; i<ITERATIONS; i++)
			if (loHalf.match())
				nbMatches++;

		double ratio = nbMatches / (double) ITERATIONS;
		assertEquals(0.5, ratio, 0.01);
	}

	@Test
	void predicateMatch() {
		final int SIZE = 10_000;

		int[] randomInts = IntStream.range(0, SIZE)
			.filter(loHalf::match)
			.toArray();
		double ratio = randomInts.length / (double) SIZE;
		assertEquals(0.5, ratio, 0.01);

		int[] empty = IntStream.range(0, SIZE)
			.filter(loImpossible::match)
			.toArray();
		assertEquals(0, empty.length);

		int[] full = IntStream.range(0, SIZE)
		.filter(loCertain::match)
		.toArray();
		assertEquals(SIZE, full.length);
	}

	@Test
	void isImpossible() {
		LogOdds lo1 = new LogOdds(-Double.MAX_VALUE);
		assertTrue(loImpossible.isImpossible());
		assertFalse(        lo1.isImpossible());
		assertFalse(     loHalf.isImpossible());
		assertFalse(  loCertain.isImpossible());
	}

	@Test
	void isCertain() {
		LogOdds lo1 = new LogOdds(Double.MAX_VALUE);
		assertTrue(    loCertain.isCertain());
		assertFalse(         lo1.isCertain());
		assertFalse(      loHalf.isCertain());
		assertFalse(loImpossible.isCertain());
	}

	@Test
	void toProbability() {
		Probability pImpossible = loImpossible.toProbability();
		Probability pHalf       = loHalf.toProbability();
		Probability pCertain    = loCertain.toProbability();
		assertEquals(0.0, pImpossible.value());
		assertEquals(0.5, pHalf.value());
		assertEquals(1.0, pCertain.value());
	}

	@Test
	void toOdds() {
		Odds oImpossible = loImpossible.toOdds();
		Odds oHalf       = loHalf.toOdds();
		Odds oCertain    = loCertain.toOdds();
		assertEquals(0.0, oImpossible.value());
		assertEquals(1.0, oHalf.value());
		assertEquals(Double.POSITIVE_INFINITY, oCertain.value());
	}

	@Test
	void toLogOdds() {
		assertTrue(loImpossible == loImpossible.toLogOdds());
		assertTrue(loHalf       == loHalf.toLogOdds());
		assertTrue(loCertain    == loCertain.toLogOdds());
	}

	@Test
	void compareTo() {
		LogOdds same   = new LogOdds(0.0);
		LogOdds lower  = new LogOdds(0.0 - EPSILON);
		LogOdds higher = new LogOdds(0.0 + EPSILON);
		assertTrue(loHalf.compareTo(  same.toProbability()) == 0);
		assertTrue(loHalf.compareTo( lower.toProbability()) >  0);
		assertTrue(loHalf.compareTo(higher.toProbability()) <  0);
		assertTrue(loHalf.compareTo(  same.toOdds()) == 0);
		assertTrue(loHalf.compareTo( lower.toOdds()) >  0);
		assertTrue(loHalf.compareTo(higher.toOdds()) <  0);
		assertTrue(loHalf.compareTo(  same) == 0);
		assertTrue(loHalf.compareTo( lower) >  0);
		assertTrue(loHalf.compareTo(higher) <  0);
	}

	@Test
	@SuppressWarnings("all") // to test equals() against unrelated type without having information dialog.
	void equals() {
		assertTrue(loImpossible.equals(new LogOdds(Double.NEGATIVE_INFINITY)));
		assertTrue(   loCertain.equals(new LogOdds(Double.POSITIVE_INFINITY)));
		assertTrue(      loHalf.equals(new LogOdds(0.0)));
		assertFalse(loHalf.equals(new LogOdds(0 + EPSILON)));
		assertFalse(loHalf.equals(new LogOdds(0 - EPSILON)));
		assertFalse(loHalf.equals(null));
		assertFalse(loHalf.equals("not even a number"));
	}

	@Test
	void LogOddsToString() {
		assertEquals("-∞",    loImpossible.toString());
		assertEquals("-4.15", new LogOdds(-4.15).toString());
		assertEquals("0.0",   new LogOdds(-0.0).toString());
		assertEquals("0.0",   new LogOdds(+0.0).toString());
		assertEquals("+15.3", new LogOdds(+15.3).toString());
		assertEquals("+∞",    loCertain.toString());
	}

	@Test
	void serialization() throws IOException, ClassNotFoundException {
		String fileName = "probability serialization.txt";
		LogOdds loOut = new LogOdds(0.564);

		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(loOut);
		objectOutputStream.flush();
		objectOutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		LogOdds loIn = (LogOdds) objectInputStream.readObject();
		objectInputStream.close();

		assertTrue(loOut.value() == loIn.value());

		// deleting created file
		File f = new File(fileName);
		f.delete();
	}
}
