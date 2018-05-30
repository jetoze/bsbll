package bsbll.stats;

import static bsbll.stats.Batting.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

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
    
    @Test(expected = IllegalArgumentException.class)
    public void ctorRejectsDerivedStats() {
        new BattingStats(ImmutableMap.of(GAMES, 1, AT_BATS, 4));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ctorRejectsNegativeValues() {
        new BattingStats(ImmutableMap.of(GAMES, 1, AT_BATS, -4));
    }
    
    @Test
    public void testAdd() {
        BattingStats willieMays1956 = BattingStats.builder()
                .set(GAMES, 152)
                .set(PLATE_APPEARANCES, 651)
                .set(RUNS, 101)
                .set(HITS, 171)
                .set(DOUBLES, 27)
                .set(TRIPLES, 8)
                .set(HOMERUNS, 36)
                .set(RUNS_BATTED_IN, 84)
                .set(WALKS, 68)
                .set(STRIKEOUTS, 65)
                .set(HIT_BY_PITCHES, 1)
                .set(SACRIFICE_FLIES, 3)
                .build();
        BattingStats total = willieMays1955.add(willieMays1956);
        
        BattingStats expected = BattingStats.builder()
                .set(GAMES, 304)
                .set(PLATE_APPEARANCES, 1321)
                .set(RUNS, 224)
                .set(HITS, 356)
                .set(DOUBLES, 45)
                .set(TRIPLES, 21)
                .set(HOMERUNS, 87)
                .set(RUNS_BATTED_IN, 211)
                .set(WALKS, 147)
                .set(STRIKEOUTS, 125)
                .set(HIT_BY_PITCHES, 5)
                .set(SACRIFICE_FLIES, 10).build();
        assertEquals(expected, total);
    }
}
