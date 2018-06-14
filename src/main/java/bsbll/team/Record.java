package bsbll.team;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import bsbll.stats.Average;

@Immutable
public final class Record {
    private final WLT wlt;
    private final RunDifferential runDiff;

    public Record() {
        this(new WLT(0, 0), new RunDifferential(0, 0));
    }
    
    public Record(WLT wlt, RunDifferential runDiff) {
        this.wlt = requireNonNull(wlt);
        this.runDiff = requireNonNull(runDiff);
    }

    public WLT getWlt() {
        return wlt;
    }
    
    public int getGamesPlayed() {
        return wlt.getNumberOfGames();
    }
    
    public int getWins() {
        return wlt.getWins();
    }
    
    public int getLosses() {
        return wlt.getLosses();
    }
    
    public int getTies() {
        return wlt.getTies();
    }
    
    public Average getWinPct() {
        return wlt.getWinPct();
    }
    
    public GamesBehind getGamesBehind(Record other) {
        return wlt.gamesBehind(other.wlt);
    }

    public RunDifferential getRunDifferential() {
        return runDiff;
    }
    
    public int getRunsScored() {
        return runDiff.getRunsScored();
    }
    
    public int getRunsAgainst() {
        return runDiff.getRunsAgainst();
    }
    
    public Record plus(Record other) {
        return new Record(this.wlt.plus(other.wlt), this.runDiff.plus(other.runDiff));
    }
    
    public Record plus(RunDifferential gameScore) {
        WLT newWlt = wlt.plus(gameScore);
        RunDifferential newRunDiff = runDiff.plus(gameScore);
        return new Record(newWlt, newRunDiff);
    }
    
    @Override
    public String toString() {
        return wlt + " " + runDiff;
    }
}
