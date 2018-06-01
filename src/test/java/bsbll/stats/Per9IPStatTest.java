package bsbll.stats;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for Per9IPStat.
 *
 */
public final class Per9IPStatTest {

    @Test
    public void oneRunPer9IPToString() {
        Per9IPStat era = new Per9IPStat(1, 27);
        
        assertEquals("1.00", era.toString());
    }
    
    @Test
    public void fiveRunsPer27IPToString() {
        Per9IPStat era = new Per9IPStat(5, 81);
        
        assertEquals("1.67", era.toString());
    }
    
    @Test
    public void oneRunWithNoOutsToString() {
        Per9IPStat era = new Per9IPStat(1, 0);
        
        assertEquals("----", era.toString());
    }
    
    @Test
    public void sorting() {
        Per9IPStat a1 = new Per9IPStat(5, 100);
        Per9IPStat a2 = new Per9IPStat(5, 80);
        Per9IPStat a3 = new Per9IPStat(5, 50);
        Per9IPStat a4 = new Per9IPStat(5, 0);
        
        List<Per9IPStat> list = Arrays.asList(a3, a2, a4, a1);
        list.sort(Comparator.<Per9IPStat>naturalOrder());
        
        assertEquals(Arrays.asList(a1, a2, a3, a4), list);
    }
}
