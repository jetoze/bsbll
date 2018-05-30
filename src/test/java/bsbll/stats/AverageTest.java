package bsbll.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for the Average class.
 */
public final class AverageTest {

    @Test
    public void ohForOhToString() {
        Average avg = new Average(0, 0);
        
        assertEquals("----", avg.toString());
    }
    
    @Test
    public void ohForOneToString() {
        Average avg = new Average(0, 1);
        
        assertEquals(".000", avg.toString());
    }
    
    @Test
    public void oneForOneToString() {
        Average avg = new Average(1, 1);
        
        assertEquals("1.000", avg.toString());
    }
    
    @Test
    public void oneForTwoToString() {
        Average avg = new Average(1, 2);
        
        assertEquals(".500", avg.toString());
    }
    
    @Test
    public void oneForThreeToString() {
        Average avg = new Average(1, 3);
        
        assertEquals(".333", avg.toString());
    }
    
    @Test
    public void oneForFourToString() {
        Average avg = new Average(1, 4);
        
        assertEquals(".250", avg.toString());
    }
    
    @Test
    public void ohForOhIsLessThanOhForOne() {
        Average na = new Average(0, 0);
        Average ohForOne = new Average(0, 1);
        
        assertTrue(na.compareTo(ohForOne) < 0);
    }
    
    @Test
    public void sorting() {
        Average a1 = new Average(5, 10);
        Average a2 = new Average(3, 10);
        Average a3 = new Average(0, 10);
        Average a4 = new Average(0, 0);
        
        List<Average> list = Arrays.asList(a3, a2, a4, a1);
        list.sort(Comparator.<Average>naturalOrder().reversed());
        
        assertEquals(Arrays.asList(a1, a2, a3, a4), list);
    }
    
    @Test
    public void sumOfWithSameDenominator() {
        Average a1 = new Average(10, 100);
        Average a2 = new Average(7, 100);
        Average sum = Average.sumOf(a1, a2);
        
        assertEquals(".170", sum.toString());
    }
    
    @Test
    public void sumOfWithDifferentDenominators() {
        Average a1 = new Average(1, 5);
        Average a2 = new Average(30, 100);
        Average sum = Average.sumOf(a1, a2);
        
        assertEquals(".500", sum.toString());
    }

}
