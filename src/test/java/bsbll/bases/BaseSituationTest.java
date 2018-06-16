package bsbll.bases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.BaseRunner;
import bsbll.player.Player;

/**
 * Unit tests for BaseSituation.
 */
public final class BaseSituationTest {
    private static final Player PITCHER = new Player("Pitcher", "Walter Johnson");
    private static final BaseRunner BATTER = new BaseRunner(new Player("Batter", "Bob Doe"), PITCHER);
    private static final BaseRunner RUNNER_A = new BaseRunner(new Player("Runner A", "Adam Doe"), PITCHER);
    private static final BaseRunner RUNNER_B = new BaseRunner(new Player("Runner B", "Bill Doe"), PITCHER);
    private static final BaseRunner RUNNER_C = new BaseRunner(new Player("Runner C", "Charlie Doe"), PITCHER);

    
    @Test(expected = InvalidBaseSitutationException.class)
    public void cannotApplyAdvancesToIncompatibleBaseSituation() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        Advances advances = Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        before.advanceRunners(BATTER, advances);
    }
    
    @Test
    public void runnersStayingPut() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Advances advances = Advances.of(
                Advance.safe(Base.FIRST, Base.FIRST),
                Advance.safe(Base.THIRD, Base.THIRD)
        );
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertEquals(before, after);
    }
    
    @Test
    public void singleWithRunnersOnFirstAndThirdScoresRunnerOnThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
        
        ImmutableList<BaseRunner> runs = before.advanceRunners(BATTER, advances).getRunnersThatScored();
        
        assertEquals(ImmutableList.of(RUNNER_B), runs);
    }
    
    @Test
    public void singleWithRunnersOnFirstAndThird() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertEquals(new BaseSituation(BATTER, null, RUNNER_A), after);
    }
    
    @Test
    public void twoRunnersThrownOutAtHomeOnSingle() {
        BaseSituation before = new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.THIRD),
                Advance.out(Base.SECOND, Base.HOME),
                Advance.out(Base.THIRD, Base.HOME));
        
        ResultOfAdvance result = before.advanceRunners(BATTER, advances);
        
        assertEquals(0, result.getNumberOfRuns());
        assertEquals(new BaseSituation(BATTER, null, RUNNER_A), result.getNewSituation());
    }
    
    @Test
    public void pickOffRemovesRunner() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Advances advances = Advances.of(Advance.out(Base.FIRST, Base.FIRST));
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertEquals(ImmutableMap.of(Base.THIRD, RUNNER_B), after.toMap());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleLeavesOnlyBatterOnBase() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, null);
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertEquals(new BaseSituation(BATTER, null, null), after);
    }
    
    @Test
    public void testGrandSlamScores4() {
        BaseSituation before = basesLoaded();
        Advances advances = grandSlam();

        ImmutableList<BaseRunner> runs = before.advanceRunners(BATTER, advances).getRunnersThatScored();
        assertEquals(Arrays.asList(RUNNER_C, RUNNER_B, RUNNER_A, BATTER), runs);
    }
    
    @Test
    public void testGrandSlamClearsBases() {
        BaseSituation before = basesLoaded();
        Advances advances = grandSlam();
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertTrue(after.isEmpty());
    }
    
    private static Advances grandSlam() {
        return Advances.of(
                Advance.safe(Base.HOME, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.THIRD, Base.HOME)
        );
    }

    private static BaseSituation basesLoaded() {
        return new BaseSituation(RUNNER_A, RUNNER_B, RUNNER_C);
    }
}
