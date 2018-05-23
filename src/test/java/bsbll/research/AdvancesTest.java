package bsbll.research;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import bsbll.Base;
import bsbll.game.BaseSituation;
import bsbll.player.Player;
import bsbll.player.PlayerId;

/**
 * Unit test for {@code Advances}.
 */
public final class AdvancesTest {
    private static final Player BATTER = new Player(PlayerId.of("Batter"));
    private static final Player RUNNER_A = new Player(PlayerId.of("Runner A"));
    private static final Player RUNNER_B = new Player(PlayerId.of("Runner B"));
    private static final Player RUNNER_C = new Player(PlayerId.of("Runner C"));
    
    @Test
    public void testGrandSlamScores4() {
        Advances advances = grandSlam();
        
        assertEquals(4, advances.getNumberOfRuns());
    }
    
    @Test
    public void testGrandSlamClearsBases() {
        BaseSituation before = basesLoaded();
        Advances advances = grandSlam();
        
        BaseSituation after = advances.applyTo(BATTER, before);
        
        assertTrue(after.isEmpty());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleScoresNone() {
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        assertEquals(0, advances.getNumberOfRuns());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleIsAnOut() {
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        assertEquals(1, advances.getNumberOfOuts());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleLeavesOnlyBatterOnBase() {
        BaseSituation before = new BaseSituation(null, RUNNER_A, null);
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        BaseSituation after = advances.applyTo(BATTER, before);
        
        assertEquals(ImmutableMap.of(Base.FIRST, BATTER), after.toMap());
    }
    
    @Test
    public void testDoublePlayResultsInTwoOuts() {
        Advances advances = Advances.of(
                Advance.out(Base.HOME, Base.FIRST),
                Advance.out(Base.FIRST, Base.SECOND)
        );
        
        assertEquals(2, advances.getNumberOfOuts());
    }

    @Test(expected = IllegalArgumentException.class)
    public void moreThanOneRunnerFromTheSameBaseIsNotAllowed() {
        Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.out(Base.FIRST, Base.THIRD)
        );
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void twoRunnersOnTheSameBaseIsNotAllowed() {
        Advances.of(
                Advance.safe(Base.FIRST, Base.THIRD),
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME)
        );
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotApplyAdvancesToIncompatibleBaseSituation() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, null);
        Advances advances = Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        advances.applyTo(BATTER, before);
    }
    
    @Test
    public void runnersStayingPut() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Map<Base, Player> mapBefore = before.toMap();
        Advances advances = Advances.of(
                Advance.safe(Base.FIRST, Base.FIRST),
                Advance.safe(Base.THIRD, Base.THIRD)
        );
        
        BaseSituation after = advances.applyTo(BATTER, before);
        
        assertEquals(mapBefore, after.toMap());
    }
    
    @Test
    public void pickOffRemovesRunner() {
        BaseSituation before = new BaseSituation(RUNNER_A, null, RUNNER_B);
        Advances advances = Advances.of(Advance.out(Base.FIRST, Base.FIRST));
        
        BaseSituation after = advances.applyTo(BATTER, before);
        
        assertEquals(ImmutableMap.of(Base.THIRD, RUNNER_B), after.toMap());
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
