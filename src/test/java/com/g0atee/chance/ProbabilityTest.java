package com.g0atee.chance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProbabilityTest {
	Probability pImpossible = new Probability(0.0);
	Probability pHalf       = new Probability(0.5);
	Probability pCertain    = new Probability(1.0);

	@Test
	public void ConstructorsTest() {
		double tooLow  = 0.0 - Double.MIN_VALUE;
		double tooHigh = 1.000000001;
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
		assertEquals(0.564, p1.value, Double.MIN_VALUE);

		// testing impossible() & certain() static factory methods
		Probability p2 = Probability.impossible();
		Probability p3 = Probability.certain();
		assertEquals(0.0, p2.value, Double.MIN_VALUE);
		assertEquals(1.0, p3.value(), Double.MIN_VALUE);
	}

	@Test
	public void complementTest() {
		assertEquals(1.0, pImpossible.complement().value(), Double.MIN_VALUE);
		assertEquals(0.0, pCertain.complement().value(), Double.MIN_VALUE);
		assertEquals(pHalf.value(), pHalf.complement().value(), Double.MIN_VALUE);

		// Normal use case
		Probability p1 = new Probability(0.347);
		Probability p1c = p1.complement();
		assertEquals(1 - 0.347, p1c.value(), Double.MIN_VALUE);
	}
}
