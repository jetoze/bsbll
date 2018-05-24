package bsbll.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import bsbll.game.BaseSituation.ResultOfAdvance;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;

/**
 * Unit tests for BaseSituation.
 */
public final class BaseSituationTest {
    private static final Player BATTER = new Player(PlayerId.of("BATTER"));
    private static final Player RUNNER_A = new Player(PlayerId.of("RUNNER_A"));
    private static final Player RUNNER_B = new Player(PlayerId.of("RUNNER_B"));
    private static final Player RUNNER_C = new Player(PlayerId.of("RUNNER_C"));
    
    @Test
    public void walkWithNoOneOnBase() {
        BaseSituation before = BaseSituation.empty();
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, null, null), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void walkWithRunerOnFirst() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, null), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void walkWithRunnersOnFirstAndSecond() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void walkWithBasesLoaded() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of(RUNNER_C));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void walkWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void walkWithRunnersOnSecondAndThird() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.WALK);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithNoOneOnBase() {
        BaseSituation before = BaseSituation.empty();
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, null, null), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnerOnFirst() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, null), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnerOnSecond() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, null, RUNNER_A), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnerOnThird() {
        BaseSituation before = new BaseSituation(null, null, RUNNER_A);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, null, null), ImmutableSet.of(RUNNER_A));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnersOnFirstAndSecond() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, null), ImmutableSet.of(RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithRunnersOnSecondAndThird() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, null, RUNNER_A), ImmutableSet.of(RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void singleWithBasesLoaded() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.SINGLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(BATTER, RUNNER_A, RUNNER_B), ImmutableSet.of(RUNNER_C));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithNoOneOnBase() {
        BaseSituation before = BaseSituation.empty();
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, null), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnerOnFirst() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, RUNNER_A), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnerOnSecond() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, null), ImmutableSet.of(RUNNER_A));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnerOnThird() {
        BaseSituation before = new BaseSituation(null, null, RUNNER_A);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, null), ImmutableSet.of(RUNNER_A));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnersOnFirstAndSecond() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, RUNNER_A), ImmutableSet.of(RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, RUNNER_A), ImmutableSet.of(RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithRunnersOnSecondAndThird() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, null), ImmutableSet.of(RUNNER_A, RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void doubleWithBasesLoaded() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.DOUBLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, BATTER, RUNNER_A), ImmutableSet.of(RUNNER_B, RUNNER_C));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void tripleWithNoOneOnBase() {
        BaseSituation before = BaseSituation.empty();
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.TRIPLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, null, BATTER), ImmutableSet.of());
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void tripleWithRunnerOnFirst() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.TRIPLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, null, BATTER), ImmutableSet.of(RUNNER_A));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void tripleWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.TRIPLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, null, BATTER), ImmutableSet.of(RUNNER_A, RUNNER_B));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void tripleWithBasesLoaded() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.TRIPLE);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                new BaseSituation(null, null, BATTER), ImmutableSet.of(RUNNER_A, RUNNER_B, RUNNER_C));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void homerunWithNoOneOnBase() {
        BaseSituation before = BaseSituation.empty();
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.HOMERUN);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                BaseSituation.empty(), ImmutableSet.of(BATTER));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void homerunWithRunnerOnFirst() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.HOMERUN);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                BaseSituation.empty(), ImmutableSet.of(RUNNER_A, BATTER));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void homerunWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.HOMERUN);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                BaseSituation.empty(), ImmutableSet.of(RUNNER_A, RUNNER_B, BATTER));
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void homerunWithBasesLoaded() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        
        ResultOfAdvance result = before.advanceRunners(BATTER, Outcome.HOMERUN);
        
        ResultOfAdvance expectedResult = new ResultOfAdvance(
                BaseSituation.empty(), ImmutableSet.of(RUNNER_A, RUNNER_B, RUNNER_C, BATTER));
        assertEquals(expectedResult, result);
    }    
}
