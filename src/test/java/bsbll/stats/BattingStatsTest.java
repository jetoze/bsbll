package bsbll.stats;

import static bsbll.stats.Batting.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for BattingStats.
 */
public final class BattingStatsTest {
    private static final BattingStats willieMays1955 = BattingStats.builder()
            .set(GAMES, 152)
            .set(PLATE_APPEARANCES, 670)
            .set(RUNS, 123)
            .set(HITS, 185)
            .set(DOUBLES, 18)
            .set(TRIPLES, 13)
            .set(HOMERUNS, 51)
            .set(RUNS_BATTED_IN, 127)
            .set(WALKS, 79)
            .set(STRIKEOUTS, 60)
            .set(HIT_BY_PITCHES, 4)
            // We're not setting this value to test the behavior of looking up a stat
            // that has not been set explicitly.
            //.set(Batting.SACRIFICE_HITS, 0)
            .set(Batting.SACRIFICE_FLIES, 7)
            .build();
    
    @Test
    public void lookupOfStatThatHasNotBeenSet() {
        assertEquals(0, willieMays1955.get(SACRIFICE_HITS));
    }

    @Test
    public void testAtBats() {
        assertEquals(580, willieMays1955.getAtBats());
    }
    
    @Test
    public void testExtraBaseHits() {
        assertEquals(82, willieMays1955.getExtraBaseHits());
    }

    @Test
    public void testSingles() {
        assertEquals(103, willieMays1955.getSingles());
    }
    
    @Test
    public void testTotalBases() {
        assertEquals(382, willieMays1955.getTotalBases());
    }
    
    @Test
    public void testBattingAverage() {
        assertEquals(".319", willieMays1955.getBattingAverage().toString());
    }
    
    @Test
    public void testSluggingAverage() {
        assertEquals(".659", willieMays1955.getSluggingPercentage().toString());
    }
    
    @Test
    public void testOnBasePercentage() {
        assertEquals(".400", willieMays1955.getOnBasePercentage().toString());
    }
    
    @Test
    public void testOps() {
        assertEquals("1.059", willieMays1955.getOps().toString());
    }
    
}
