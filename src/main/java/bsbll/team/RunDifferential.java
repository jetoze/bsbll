package bsbll.team;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

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
    
    public RunDifferential add(RunDifferential other) {
        return addScore(other.runsScored, other.runsAgainst);
    }
    
    public RunDifferential addScore(int runsScored, int runsAgainst) {
        return new RunDifferential(this.runsScored + runsScored, this.runsAgainst + runsAgainst);
    }
    
    @Override
    public String toString() {
        return runsScored + " - " + runsAgainst;
    }
}
