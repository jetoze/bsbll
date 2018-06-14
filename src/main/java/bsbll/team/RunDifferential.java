package bsbll.team;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class RunDifferential {
    private final int runsScored;
    private final int runsAgainst;

    public RunDifferential(int runsScored, int runsAgainst) {
        this.runsScored = checkNotNegative(runsScored);
        this.runsAgainst = checkNotNegative(runsAgainst);
    }

    public int getRunsScored() {
        return runsScored;
    }

    public int getRunsAgainst() {
        return runsAgainst;
    }
    
    /**
     * When representing the score of a game, does this RunDifferential 
     * correspond to a win?
     */
    public boolean isWin() {
        return runsScored > runsAgainst;
    }
    
    /**
     * When representing the score of a game, does this RunDifferential 
     * correspond to a loss?
     */
    public boolean isLoss() {
        return runsScored < runsAgainst;
    }
    
    /**
     * When representing the score of a game, does this RunDifferential 
     * correspond to a tie?
     */
    public boolean isTie() {
        return runsScored == runsAgainst;
    }
    
    public RunDifferential plus(RunDifferential other) {
        return plusScore(other.runsScored, other.runsAgainst);
    }
    
    public RunDifferential plusScore(int runsScored, int runsAgainst) {
        return new RunDifferential(this.runsScored + runsScored, this.runsAgainst + runsAgainst);
    }
    
    public RunDifferential reverse() {
        return (runsScored != runsAgainst)
                ? new RunDifferential(runsAgainst, runsScored)
                : this;
    }
    
    @Override
    public String toString() {
        return String.format("%dR %dRA", runsScored, runsAgainst);
    }
}
