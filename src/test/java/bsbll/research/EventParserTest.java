package bsbll.research;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for EventParser.
 */
public final class EventParserTest {
    @Test
    public void testSingleWithEmptyBases() {
        PlayOutcome outcome = EventParser.parse("S");
        PlayOutcome expected = PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testSingleWithEmptyBasesAndBatterAdvancementGivenExplicitly() {
        PlayOutcome outcome = EventParser.parse("S.B-1");
        PlayOutcome expected = PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testSingleWithRunnerOnFirstAndSecond() {
        PlayOutcome outcome = EventParser.parse("S7.2-H;1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.SECOND, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("CS2");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withOut(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testErrorOnCaughtStealingWithExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4).1-3");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testErrorOnCaughtStealingWithoutExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4)");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void caughtStealingHomeNegatedByObstruction() {
        PlayOutcome outcome = EventParser.parse("CSH(E2)/OBS.3-H(UR);1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withErrors(1)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void testErrorOnDoubleWithErrorNotationInTheAdvanceSection() {
        PlayOutcome outcome = EventParser.parse("D7.1-H;B-3(E5/THH)");
        PlayOutcome expected = PlayOutcome.builder(EventType.DOUBLE)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .withSafeAdvance(Base.HOME, Base.THIRD)
                .build();
        assertEquals(expected, outcome);
    }
    
    @Test
    public void strikeoutIsOneOut() {
        PlayOutcome outcome = EventParser.parse("K");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void strikeoutPlusCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("K+CS2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STRIKEOUT)
                .withOut(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void strikeoutPlusCaughtStealingResultsInTwoOuts() {
        PlayOutcome outcome = EventParser.parse("K+CS2");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void testFlyOutIsOneOut() {
        PlayOutcome outcome = EventParser.parse("8");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }

}
