package bsbll.game;

import static com.google.common.base.Preconditions.checkState;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

import javax.annotation.concurrent.Immutable;

/**
 * The number of runs needed for a team to win the game in the bottom of the
 * ninth inning or later.
 */
@Immutable
public final class RunsNeededToWin { // TODO: Rename --> WinsNeededToWalkOff?
    /**
     * Used in half-innings that are not subject for walk-offs, such as the top of an inning,
     * or the bottom of the eigth inning or earlier.
     */
    private static final RunsNeededToWin NOT_APPLICABLE = new RunsNeededToWin(-1);
    /**
     * Used to represent the case where the game has been won on a walk-off.
     */
    private static final RunsNeededToWin GAME_WON = new RunsNeededToWin(0); 
    
    private final int runsNeeded;
    
    private RunsNeededToWin(int runs) {
        this.runsNeeded = runs;
    }
    
    /**
     * Returns a {@code RunsNeededToWin} instance for the given number of runs needed to win.
     */
    public static RunsNeededToWin of(int runs) {
        checkPositive(runs);
        // TODO: We could cache some common instances, e.g. all RunsNeededToWin with values between 1-5.
        return (runs > 0)
                ? new RunsNeededToWin(runs)
                : NOT_APPLICABLE;
    }
    
    /**
     * The {@code RunsNeededToWin} instance to use in half-innings that can't end in a walk-off.
     */
    public static RunsNeededToWin notApplicable() {
        return NOT_APPLICABLE;
    }

    /**
     * Checks if the game has been won in a walk-off.
     */
    public boolean isGameOver() {
        return this == GAME_WON;
    }
    
    /**
     * Updates the runs needed to win.
     * 
     * @param runsScored
     *            the number of runs that scored, if any. ({@code 0} (zero) is
     *            OK, in which case {@code this} instance is returned.
     * @return the resulting {@code RunsNeededToWin} ({@code this} instance is
     *         not modified).
     */
    public RunsNeededToWin updateWithRunsScored(int runsScored) {
        checkNotNegative(runsScored);
        checkState(this != GAME_WON, "The game has already been won");
        if (this == NOT_APPLICABLE) {
            return NOT_APPLICABLE;
        } else if (runsScored == 0) {
            return this;
        } else if (runsScored >= this.runsNeeded) {
            return GAME_WON;
        } else {
            return of(this.runsNeeded - runsScored);
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.runsNeeded);
    }
}
