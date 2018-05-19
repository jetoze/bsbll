package bsbll.card;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for Probability.
 */
public final class ProbabilityTest {

    @Test
    public void testFiftyFifty() {
        Probability p = Probability.of(50, 100);
        assertFalse(p.test(i -> fixedDie(i)));
        assertTrue(p.test(i -> fixedDie(1)));
        assertTrue(p.test(i -> fixedDie(i / 2)));
    }
    
    @Test
    public void complementOfZeroIsComplete() {
        assertEquals(Probability.COMPLETE, Probability.complementOf(Probability.ZERO));
    }
    
    @Test
    public void complementOfCompleteIsZero() {
        assertEquals(Probability.ZERO, Probability.complementOf(Probability.COMPLETE));
    }
    
    private static Die fixedDie(int value) {
        return () -> value;
    }
    
}
