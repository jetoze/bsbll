package bsbll.stats;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for ERA.
 *
 */
public final class ERATest {

    @Test
    public void oneRunPer9IPToString() {
        ERA era = new ERA(1, 27);
        
        assertEquals("1.00", era.toString());
    }
    
    @Test
    public void fiveRunsPer27IPToString() {
        ERA era = new ERA(5, 81);
        
        assertEquals("1.67", era.toString());
    }
    
    @Test
    public void oneRunWithNoOutsToString() {
        ERA era = new ERA(1, 0);
        
        assertEquals("----", era.toString());
    }
    
    @Test
    public void sorting() {
        ERA a1 = new ERA(5, 100);
        ERA a2 = new ERA(5, 80);
        ERA a3 = new ERA(5, 50);
        ERA a4 = new ERA(5, 0);
        
        List<ERA> list = Arrays.asList(a3, a2, a4, a1);
        list.sort(Comparator.<ERA>naturalOrder());
        
        assertEquals(Arrays.asList(a1, a2, a3, a4), list);
    }

}
