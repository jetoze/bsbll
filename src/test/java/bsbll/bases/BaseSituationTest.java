package bsbll.bases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.player.Player;

/**
 * Unit tests for BaseSituation.
 */
public final class BaseSituationTest {
    private static final Player BATTER = new Player("Batter", "Bob Doe");
    private static final Player RUNNER_A = new Player("Runner A", "Adam Doe");
    private static final Player RUNNER_B = new Player("Runner B", "Bill Doe");
    private static final Player RUNNER_C = new Player("Runner C", "Charlie Doe");

    
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
        Map<Base, Player> mapBefore = before.toMap();
        Advances advances = Advances.of(
                Advance.safe(Base.FIRST, Base.FIRST),
                Advance.safe(Base.THIRD, Base.THIRD)
        );
        
        BaseSituation after = before.advanceRunners(BATTER, advances).getNewSituation();
        
        assertEquals(mapBefore, after.toMap());
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
        
        assertEquals(ImmutableMap.of(Base.FIRST, BATTER), after.toMap());
    }
    
    @Test
    public void testGrandSlamScores4() {
        BaseSituation before = basesLoaded();
        Advances advances = grandSlam();

        ImmutableList<Player> runs = before.advanceRunners(BATTER, advances).getRunnersThatScored();
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
