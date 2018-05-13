package bsbll.research;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Unit test for EventParser.
 */
public final class EventParserTest {
    private static final Player BATTER = new Player();
    private static final Player RUNNER_A = new Player();
    private static final Player RUNNER_B = new Player();
    private static final Player RUNNER_C = new Player();
    
    // TODO: Split up these tests, perhaps into individual test classes for each event type.
    // The problem here is that we have too many asserts in each test. Alternatively,
    // implements PlayOutcome.equals, and update each test to build the expected
    // outcome and compare them.
    
    @Test
    public void testSingleWithEmptyBases() {
        PlayOutcome outcome = EventParser.parse("S");
        assertSame(EventType.SINGLE, outcome.getType());
        assertEquals(0, outcome.getNumberOfOuts());
        assertEquals(0, outcome.getNumberOfRuns());
        assertEquals(0, outcome.getNumberOfErrors());
        BaseSituation newSituation = outcome.advanceRunners(BATTER, BaseSituation.empty());
        assertEquals(1, newSituation.getNumberOfRunners());
        assertTrue(newSituation.isOccupied(Base.FIRST));
        assertSame(BATTER, newSituation.getRunner(Base.FIRST));
    }
    
    @Test
    public void testSingleWithRunnerOnFirstAndSecond() {
        PlayOutcome outcome = EventParser.parse("S7.2-H;1-2");
        assertSame(EventType.SINGLE, outcome.getType());
        assertEquals(0, outcome.getNumberOfOuts());
        assertEquals(1, outcome.getNumberOfRuns());
        assertEquals(0, outcome.getNumberOfErrors());
        Player wasOnFirst = RUNNER_A;
        Player wasOnSecond = RUNNER_B;
        BaseSituation before = new BaseSituation(ImmutableMap.of(Base.FIRST, wasOnFirst,
                Base.SECOND, wasOnSecond));
        BaseSituation after = outcome.advanceRunners(BATTER, before);
        assertEquals(2, after.getNumberOfRunners());
        assertTrue(after.isOccupied(Base.FIRST));
        assertTrue(after.isOccupied(Base.SECOND));
        assertSame(BATTER, after.getRunner(Base.FIRST));
        assertSame(wasOnFirst, after.getRunner(Base.SECOND));
    }
    
    @Test
    public void testCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("CS2");
        assertSame(EventType.CAUGHT_STEALING, outcome.getType());
        assertEquals(1, outcome.getNumberOfOuts());
        assertEquals(0, outcome.getNumberOfRuns());
        assertEquals(0, outcome.getNumberOfErrors());
        assertEquals(Collections.singleton(Base.FIRST), outcome.getOuts());
    }
    
    @Test
    public void testErrorOnCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4).1-3");
        assertSame(EventType.CAUGHT_STEALING, outcome.getType());
        assertEquals(0, outcome.getNumberOfOuts());
        assertEquals(0, outcome.getNumberOfRuns());
        assertEquals(1, outcome.getNumberOfErrors());
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        BaseSituation after = outcome.advanceRunners(BATTER, before);
        assertEquals(1, after.getNumberOfRunners());
        assertSame(RUNNER_A, after.getRunner(Base.THIRD));
    }

}
