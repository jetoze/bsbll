package bsbll.team;

import static java.util.Objects.requireNonNull;

import bsbll.stats.Average;

public final class Record {
    private final WLT wlt;
    private final RunDifferential runDiff;

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
    
    public Record add(Record other) {
        return new Record(this.wlt.add(other.wlt), this.runDiff.add(other.runDiff));
    }
    
    @Override
    public String toString() {
        return wlt + " " + runDiff;
    }
}
