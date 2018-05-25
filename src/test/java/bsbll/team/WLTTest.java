package bsbll.team;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bsbll.stats.Average;

/**
 * Unit tests for WLT.
 */
public final class WLTTest {

    @Test
    public void tiesAreNotIncludedInWinPct() {
        WLT wlt = new WLT(8, 2, 1);
        Average winPct = wlt.getWinPct();
        
        assertEquals(0.8, winPct.asDouble(), 0.00001);
    }
    
    @Test
    public void gamesBehindEqualRecord() {
        WLT wlt = new WLT(10, 2);
        GamesBehind gb = wlt.gamesBehind(wlt);
        
        assertEquals("0.0", gb.toString());
    }
    
    @Test
    public void twoGamesBehind() {
        GamesBehind gb = new WLT(8, 4).gamesBehind(new WLT(10, 2));
        
        assertEquals("2.0", gb.toString());
    }

    @Test
    public void twoGamesBehindWithDifferentNumberOfGamesPlayed() {
        GamesBehind gb = new WLT(8, 4).gamesBehind(new WLT(12, 4));
        
        assertEquals("2.0", gb.toString());
    }
    
    @Test
    public void halfGameBehind() {
        GamesBehind gb = new WLT(8, 4).gamesBehind(new WLT(8, 3));
        
        assertEquals("0.5", gb.toString());
    }
    
    @Test
    public void halfGameAhead() {
        GamesBehind gb = new WLT(8, 4).gamesBehind(new WLT(8, 5));
        
        assertEquals("-0.5", gb.toString());
    }
    
    public void oneAndAHalfGameAhead() {
        GamesBehind gb = new WLT(8, 2).gamesBehind(new WLT(7, 4));
        
        assertEquals("-1.5", gb.toString());
    }
    
}
