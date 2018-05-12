package bsbll.research;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Unit test for EventParser.
 */
public final class EventParserTest {
    private final Player batter = new Player();
    
    @Test
    public void testSingleWithEmptyBases() {
        PlayOutcome outcome = EventParser.parse("S");
        assertSame(EventType.SINGLE, outcome.getType());
        assertEquals(0, outcome.getNumberOfOuts());
        assertEquals(0, outcome.getNumberOfRuns());
        assertEquals(0, outcome.getNumberOfErrors());
        BaseSituation newSituation = outcome.advanceRunners(batter, BaseSituation.empty());
        assertEquals(1, newSituation.getNumberOfRunners());
        assertTrue(newSituation.isOccupied(Base.FIRST));
        assertSame(batter, newSituation.getRunner(Base.FIRST));
    }
    
    @Test
    public void testSingleWithRunnerOnFirstAndSecond() {
        PlayOutcome outcome = EventParser.parse("S7.2-H;1-2");
        assertSame(EventType.SINGLE, outcome.getType());
        assertEquals(0, outcome.getNumberOfOuts());
        assertEquals(1, outcome.getNumberOfRuns());
        assertEquals(0, outcome.getNumberOfErrors());
        Player wasOnFirst = new Player();
        Player wasOnSecond = new Player();
        BaseSituation before = new BaseSituation(ImmutableMap.of(Base.FIRST, wasOnFirst,
                Base.SECOND, wasOnSecond));
        BaseSituation after = outcome.advanceRunners(batter, before);
        assertEquals(2, after.getNumberOfRunners());
        assertTrue(after.isOccupied(Base.FIRST));
        assertTrue(after.isOccupied(Base.SECOND));
        assertSame(batter, after.getRunner(Base.FIRST));
        assertSame(wasOnFirst, after.getRunner(Base.SECOND));
    }

}
