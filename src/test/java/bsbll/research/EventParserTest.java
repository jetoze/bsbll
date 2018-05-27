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
                .withAdditionalErrors(1)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .withSafeAdvance(Base.HOME, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void reachedOnError() {
        PlayOutcome outcome = EventParser.parse("E1/TH/BG15.1-3");
        PlayOutcome expected = PlayOutcome.builder(EventType.REACHED_ON_ERROR)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .withSafeOnError(Base.HOME, Base.FIRST)
                .withErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void reachedOnErrorIsOneError() {
        PlayOutcome outcome = EventParser.parse("E1/TH/BG15.1-3");
        
        assertEquals(1, outcome.getNumberOfErrors());
    }
    
    @Test
    public void reachedOnErrorWithExplicitBatterAdvance() {
        PlayOutcome outcome = EventParser.parse("E3.1-2;B-1");
        PlayOutcome expected = PlayOutcome.builder(EventType.REACHED_ON_ERROR)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .withSafeOnError(Base.HOME, Base.FIRST)
                .withErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void reachedOnErrorWithLongerErrorNotation() {
        PlayOutcome outcome = EventParser.parse("654E3.2-H(UR)(NR)");
        PlayOutcome expected = PlayOutcome.builder(EventType.REACHED_ON_ERROR)
                .withSafeAdvance(Base.SECOND, Base.HOME)
                .withSafeOnError(Base.HOME, Base.FIRST)
                .withErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void batterThrownOutAfterReachingSafelyOnError() {
        PlayOutcome outcome = EventParser.parse("E4.1-3;BX2(86)");
        PlayOutcome expected = PlayOutcome.builder(EventType.REACHED_ON_ERROR)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .withOut(Base.HOME, Base.SECOND)
                .withErrors(1)
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
                .withAdditionalErrors(1)
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
                .withAdditionalErrors(1)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void doubleStealWithErrors() {
        PlayOutcome outcome = EventParser.parse("SB3;SB2.1-3(E6/THH)(UR);2-H(E5)");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withAdditionalErrors(2)
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
    public void twoRunnersCaughtStealing() {
        PlayOutcome outcome = EventParser.parse("CS2(24);CSH(42)/DP");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.THIRD, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void errorOnCaughtStealingWithExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4).1-3");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withAdditionalErrors(1)
                .withSafeAdvance(Base.FIRST, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void errorOnCaughtStealingWithoutExplicitAdvancement() {
        PlayOutcome outcome = EventParser.parse("CS2(2E4)");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withAdditionalErrors(1)
                .withSafeOnError(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void caughtStealingHomeNegatedByError() {
        PlayOutcome outcome = EventParser.parse("CSH(26E2)(UR).1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withSafeOnError(Base.THIRD, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void caughtStealingHomeNegatedByObstruction() {
        PlayOutcome outcome = EventParser.parse("CSH(E2)/OBS.3-H(UR);1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withAdditionalErrors(1)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void caughtStealingWithMinimalErrorNotation() {
        PlayOutcome outcome = EventParser.parse("CS2(E2)");
        PlayOutcome expected = PlayOutcome.builder(EventType.CAUGHT_STEALING)
                .withSafeOnError(Base.FIRST, Base.SECOND)
                .withAdditionalErrors(1)
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
    public void runnerIsSafeWhenPickedOffCaughtStealingIsNegatedByError() {
        PlayOutcome outcome = EventParser.parse("POCS2(1E3)");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withSafeOnError(Base.FIRST, Base.SECOND)
                .withErrors(1)
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
    public void pickoffAttemptWithError() {
        PlayOutcome outcome = EventParser.parse("PO1(13E6)#");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void pickoffAttemptWithDifferentErrorNotation() {
        PlayOutcome outcome = EventParser.parse("PO1(E2/TH).2-3");
        PlayOutcome expected = PlayOutcome.builder(EventType.PICKED_OFF)
                .withSafeAdvance(Base.SECOND, Base.THIRD)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
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
    
    @Test
    public void forceOutAtSecond() {
        PlayOutcome outcome = EventParser.parse("54(1)/FO");
        PlayOutcome expected = PlayOutcome.builder(EventType.FORCE_OUT)
                .withOut(Base.FIRST, Base.SECOND)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void forceOutAtSecondIsOneOut() {
        PlayOutcome outcome = EventParser.parse("54(1)/FO");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void batterReachesFirstWhenRunnerOnSecondIsThrownOutAtThird() {
        PlayOutcome outcome = EventParser.parse("75(2)/F7S.1-2");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .withSafeAdvance(Base.FIRST, Base.SECOND)
                .withOut(Base.SECOND, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void groundBallDoublePlay() {
        PlayOutcome outcome = EventParser.parse("64(1)3/GDP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void groundBallDoublePlayIsTwoOuts() {
        PlayOutcome outcome = EventParser.parse("64(1)3/GDP");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void doublePlayWithBothOutsGivenInBasicPlay() {
        PlayOutcome outcome = EventParser.parse("3(B)6(1)/GDP");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void flyBallDoublePlay() {
        PlayOutcome outcome = EventParser.parse("8(B)2(3)/FDP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.THIRD, Base.HOME)
                .withOut(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void flyBallDoublePlayIsTwoOuts() {
        PlayOutcome outcome = EventParser.parse("8(B)2(3)/FDP");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void lineDriveDoublePlay() {
        PlayOutcome outcome = EventParser.parse("8(B)65(2)/LDP/SF.3-H: 1");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withOut(Base.SECOND, Base.THIRD)
                .withOut(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void lineDriveDoublePlayIsTwoOuts() {
        PlayOutcome outcome = EventParser.parse("8(B)65(2)/LDP/SF.3-H: 1");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }
    
    @Test
    public void buntPoppedIntoDoublePlay() {
        PlayOutcome outcome = EventParser.parse("5(B)6(2)/BPDP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.HOME, Base.FIRST)
                .withOut(Base.SECOND, Base.THIRD)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void unspecifiedDoublePlay() {
        PlayOutcome outcome = EventParser.parse("5(3)3(B)/DP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.THIRD, Base.HOME)
                .withOut(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void unspecifiedDoublePlayIsTwoOuts() {
        PlayOutcome outcome = EventParser.parse("5(3)3(B)/DP");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }

    @Test
    public void batterIsNotOutIfBothOutsInDoublePlayAreOnBases() {
        PlayOutcome outcome = EventParser.parse("84(1)5(2)/FDP/F8S");
        // Not sure what the EventType should be for this one. This is
        // a real example from a 1925 game between Pittsburgh and St. Louis.
        // It happened with one out in the bottom of the fourth, so ended
        // the inning --> it didn't really matter what happened to the batter.
        // Stick with OUT for now, since that's what the play-by-play file
        // indicates.
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.SECOND, Base.THIRD)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void batterIsNotOutOnDoublePlayWhereTwoRunnersAreThrownOutAtHome() {
        PlayOutcome outcome = EventParser.parse("62(3)/DP.2XH(232)");
        // Not sure what the EventType should be for this one. This is
        // a real example from a 1925 game between Pittsburgh and St. Louis.
        // It happened with one out in the bottom of the fourth, so ended
        // the inning --> it didn't really matter what happened to the batter.
        // Stick with OUT for now, since that's what the play-by-play file
        // indicates.
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.THIRD, Base.HOME)
                .withOut(Base.SECOND, Base.HOME)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void bothOutsInDoublePlayAreOnBasesIsTwoOuts() {
        PlayOutcome outcome = EventParser.parse("84(1)5(2)/FDP/F8S");
        
        assertEquals(2, outcome.getNumberOfOuts());
    }

    @Test
    public void triplePlay() {
        PlayOutcome outcome = EventParser.parse("7(B)2(3)45(2)/TP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.HOME, Base.FIRST)
                .withOut(Base.SECOND, Base.THIRD)
                .withOut(Base.THIRD, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void triplePlayIsThreeOuts() {
        PlayOutcome outcome = EventParser.parse("7(B)2(3)45(2)/TP");
        
        assertEquals(3, outcome.getNumberOfOuts());
    }
    
    @Test
    public void lineDriveTriplePlay() {
        PlayOutcome outcome = EventParser.parse("7(B)743(1)32(3)/LTP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.HOME, Base.FIRST)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.THIRD, Base.HOME)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void groundBallTriplePlay() {
        PlayOutcome outcome = EventParser.parse("5(2)54(1)43(B)/GTP");
        PlayOutcome expected = PlayOutcome.builder(EventType.OUT)
                .withOut(Base.SECOND, Base.THIRD)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.HOME, Base.FIRST)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void fieldersChoiceWithRunnerSafeAtSecondOnError() {
        PlayOutcome outcome = EventParser.parse("FC4.1X2(4E6);B-1");
        PlayOutcome expected = PlayOutcome.builder(EventType.FIELDERS_CHOICE)
                .withSafeOnError(Base.FIRST, Base.SECOND)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void fieldersChoiceWithRunnerSafeAtSecondOnErrorIsNotAnOut() {
        PlayOutcome outcome = EventParser.parse("FC4.1X2(4E6);B-1");
        
        assertEquals(0, outcome.getNumberOfOuts());
    }

    @Test
    public void fieldersChoiceWithRunnerSafeAtSecondOnErrorIsAnError() {
        PlayOutcome outcome = EventParser.parse("FC4.1X2(4E6);B-1");
        
        assertEquals(1, outcome.getNumberOfErrors());
    }
    
    @Test
    public void batterIsAwardedFirstOnCatchersInterference() {
        PlayOutcome outcome = EventParser.parse("C/E2");
        PlayOutcome expected = PlayOutcome.builder(EventType.INTERFERENCE)
                .withSafeOnError(Base.HOME, Base.FIRST)
                .withErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void batterThrownOutAtThirdTryingToStretchSingleAfterError() {
        // This one is tricky. At the moment the AdvanceField is parsed completely independently,
        // but in this case it needs knowledge about the play type.
        PlayOutcome outcome = EventParser.parse("S8.BX3(E8)(845)");
        PlayOutcome expected = PlayOutcome.builder(EventType.SINGLE)
                .withOut(Base.HOME, Base.THIRD)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }

    @Test
    public void batterThrownOutAtThirdTryingToStretchSingleAfterErrorIsOneOut() {
        // This one is tricky. At the moment the AdvanceField is parsed completely independently,
        // but in this case it needs knowledge about the play type.
        PlayOutcome outcome = EventParser.parse("S8.BX3(E8)(845)");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void runnerOnFirstThrownOutAtHomeAfterSuccessfullyStealingSecond() {
        // Same concept as above
        PlayOutcome outcome = EventParser.parse("SB2.1XH(E2/TH2)(42)");
        PlayOutcome expected = PlayOutcome.builder(EventType.STOLEN_BASE)
                .withOut(Base.FIRST, Base.HOME)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }

    
    @Test
    public void runnerOnFirstThrownOutAtHomeAfterSuccessfullyStealingSecondIsOneOut() {
        // Same concept as above
        PlayOutcome outcome = EventParser.parse("SB2.1XH(E2/TH2)(42)");
        
        assertEquals(1, outcome.getNumberOfOuts());
    }
    
    @Test
    public void batterThrownOutTryingToAdvanceOnForceOutWithError() {
        PlayOutcome outcome = EventParser.parse("64(1)/FO/NDP.BX3(E4/TH1)(35)");
        PlayOutcome expected = PlayOutcome.builder(EventType.FORCE_OUT)
                .withOut(Base.FIRST, Base.SECOND)
                .withOut(Base.HOME, Base.THIRD)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void runnerOnFirstOutAtHomeTryingToScoreOnSinglePlusError() {
        PlayOutcome outcome = EventParser.parse("S1.3-H;1XH(E2/TH1)(92)");
        PlayOutcome expected = PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.THIRD, Base.HOME)
                .withOut(Base.FIRST, Base.HOME)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
    
    @Test
    public void batterScoresOnDoublePlusError() {
        PlayOutcome outcome = EventParser.parse("D7.2-H;1-H;BXH(7E5)(UR)(NR)");
        PlayOutcome expected = PlayOutcome.builder(EventType.DOUBLE)
                .withSafeAdvance(Base.SECOND, Base.HOME)
                .withSafeAdvance(Base.FIRST, Base.HOME)
                .withSafeOnError(Base.HOME, Base.HOME)
                .withAdditionalErrors(1)
                .build();
        
        assertEquals(expected, outcome);
    }
}
