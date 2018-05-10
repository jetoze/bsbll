package bsbll.research;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
