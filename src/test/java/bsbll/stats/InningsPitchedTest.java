package bsbll.stats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for InningsPitched.
 */
public class InningsPitchedTest {

    @Test
    public void twentySevenOutsToString() {
        InningsPitched ip = InningsPitched.fromOuts(27);
        
        assertEquals("9.0", ip.toString());
    }

    @Test
    public void eightOutsToString() {
        InningsPitched ip = InningsPitched.fromOuts(8);
        
        assertEquals("2.2", ip.toString());
    }
    
    @Test
    public void fourOutsToString() {
        InningsPitched ip = InningsPitched.fromOuts(4);
        
        assertEquals("1.1", ip.toString());
    }
    
    @Test
    public void zeroOutsToString() {
        InningsPitched ip = InningsPitched.fromOuts(0);
        
        assertEquals("0.0", ip.toString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeOuts() {
        new InningsPitched(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fromOutsFactoryMethodRejectsNegativeOuts() {
        InningsPitched.fromOuts(-1);
    }
    
}
