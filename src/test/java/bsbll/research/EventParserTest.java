package bsbll.research;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bsbll.Base;

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
    public void errorOnDoubleWithErrorNotationInTheAdvanceSection() {
        PlayOutcome outcome = EventParser.parse("D7.1-H;B-3(E5/THH)");
        PlayOutcome expected = PlayOutcome.builder(EventType.DOUBLE)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .withSafeAdvance(Base.HOME, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void stolenBase() {
        PlayOutcome outcome = EventParser.parse("SB2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();

        assertEquals(expected, outcome);
    }
    
    @Test
    public void stolenBaseWithAdvanceGivenExplicitly() {
        PlayOutcome outcome = EventParser.parse("SB2.1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();

        assertEquals(expected, outcome);
    }
    
    @Test
    public void stolenBaseWithError() {
        PlayOutcome outcome = EventParser.parse("SB2.1-3(E2/TH2)");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void doubleSteal() {
        PlayOutcome outcome = EventParser.parse("SB3;SB2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withSafeAdvance(Base.SECOND, Base.THIRD)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void doubleStealWithError() {
        PlayOutcome outcome = EventParser.parse("SBH;SB2.1-H(E6/THH)(UR)");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withErrors(1)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void doubleStealWithErrors() {
        PlayOutcome outcome = EventParser.parse("SB3;SB2.1-3(E6/THH)(UR);2-H(E5)");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withErrors(2)
                .withSafeAdvance(Base.SECOND, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void caughtStealing() {
        PlayOutcome outcome = EventParser.parse("CS2");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withOut(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void errorOnCaughtStealingWithExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4).1-3");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withErrors(1)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void errorOnCaughtStealingWithoutExplicitAdvancement() {
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
    public void pickedOffCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("POCSH");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withOut(Base.THIRD, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void pickoff() {
        PlayOutcome outcome = EventParser.parse("PO1(23)");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withOut(Base.FIRST, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void pickoffWithExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("PO1(23).1X1");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withOut(Base.FIRST, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void pickoffIsOneOut() {
        PlayOutcome outcome = EventParser.parse("PO1(23)");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void pickoffWithExplicitAdvancementIsOneOut() {
        PlayOutcome outcome = EventParser.parse("PO1(23).1X1");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void strikeoutIsOneOut() {
        PlayOutcome outcome = EventParser.parse("K");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void stolenBaseOnStrikeout() {
        PlayOutcome outcome = EventParser.parse("K+SB2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STRIKEOUT)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void doubleStealOnStrikeout() {
        PlayOutcome outcome = EventParser.parse("K+SB3;SB2");
        PlayOutcome expected = PlayOutcome.builder(EventType.STRIKEOUT)
                .withSafeAdvance(Base.SECOND, Base.THIRD)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void stolenBaseOnStrikeoutIsOneOut() {
        PlayOutcome outcome = EventParser.parse("K+SB2");
        
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
    public void strikeoutPlusPickOff() {
        PlayOutcome outcome = EventParser.parse("K+PO1(23)/DP");
        PlayOutcome expected = PlayOutcome.builder(EventType.STRIKEOUT)
                .withOut(Base.FIRST, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void strikeoutPlusPickOffResultsInTwoOuts() {
        PlayOutcome outcome = EventParser.parse("K+PO1(23)/DP");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void batterReachesFirstOnWildPitchStrikeout() {
        PlayOutcome outcome = EventParser.parse("K+WP.B-1");
        PlayOutcome expected = PlayOutcome.builder(EventType.STRIKEOUT)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void batterReachesFirstOnWildPitchStrikeoutIsNotAnOut() {
        PlayOutcome outcome = EventParser.parse("K+WP.B-1");
        
        assertEquals(0, outcome.getNumberOfOuts());
    }
    
    
    @Test
    public void testFlyOutIsOneOut() {
        PlayOutcome outcome = EventParser.parse("8");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }

}
