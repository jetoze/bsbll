package bsbll.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class RunsNeededToWinTest {

    @Test
    public void gameIsNeverOverWhenNotApplicable() {
        RunsNeededToWin r = RunsNeededToWin.notApplicable().updateWithRunsScored(1_000_000);
        
        assertFalse(r.isGameOver());
    }
    
    @Test
    public void gameOver() {
        RunsNeededToWin r = RunsNeededToWin.of(2);
        
        r = r.updateWithRunsScored(2);
        
        assertTrue(r.isGameOver());
    }
    
    @Test
    public void gameOverWithMoreRunsScoringThanNeededToWin() {
        RunsNeededToWin r = RunsNeededToWin.of(2);
        
        r = r.updateWithRunsScored(4);
        
        assertTrue(r.isGameOver());
    }
    
    @Test
    public void runScoredButMoreAreNeeded() {
        RunsNeededToWin r = RunsNeededToWin.of(2);
        
        r = r.updateWithRunsScored(1);
        
        assertFalse(r.isGameOver());
    }
    
    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToUpdateWhenGameIsAlreadyWon() {
        RunsNeededToWin r = RunsNeededToWin.of(1)
                .updateWithRunsScored(1);
        
        r.updateWithRunsScored(1);
    }

}
