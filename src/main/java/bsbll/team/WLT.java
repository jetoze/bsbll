package bsbll.team;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import javax.annotation.concurrent.Immutable;

import bsbll.stats.Average;

@Immutable
public final class WLT {
    private final int wins;
    private final int losses;
    private final int ties;

    public WLT(int wins, int losses) {
        this(wins, losses, 0);
    }
    
    public WLT(int wins, int losses, int ties) {
        this.wins = checkNotNegative(wins);
        this.losses = checkNotNegative(losses);
        this.ties = checkNotNegative(ties);
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }
    
    public int getNumberOfGames() {
        return this.wins + this.losses + this.ties;
    }
    
    public WLT plus(WLT other) {
        return new WLT(this.wins + other.wins, this.losses + other.losses, this.ties + other.ties);
    }
    
    public WLT plusWin() {
        return new WLT(this.wins + 1, this.losses, this.ties);
    }
    
    public WLT plusLoss() {
        return new WLT(this.wins, this.losses + 1, this.ties);
    }
    
    public WLT plusTie() {
        return new WLT(this.wins, this.losses, this.ties + 1);
    }
    
    public WLT plus(RunDifferential runDiff) {
        if (runDiff.isWin()) {
            return plusWin();
        } else if (runDiff.isLoss()) {
            return plusLoss();
        } else {
            return plusTie();
        }
    }

    public Average getWinPct() {
        // Ties are not included in the calculaton
        return new Average(wins, wins + losses);
    }
    
    public GamesBehind gamesBehind(WLT other) {
        return new GamesBehind(other.wins - this.wins + this.losses - other.losses);
    }
    
    @Override
    public String toString() {
        String winsAndLosses = String.format("%dW %dL", wins, ties);
        return (ties == 0)
                ? winsAndLosses
                : winsAndLosses + " " + ties + "T";
    }

}
