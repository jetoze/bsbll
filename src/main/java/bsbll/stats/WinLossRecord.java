package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import javax.annotation.concurrent.Immutable;

import bsbll.game.Decision;

/**
 * The win-loss record of an individual pitcher.
 */
@Immutable
public final class WinLossRecord {
    private final int wins;
    private final int losses;
    
    public WinLossRecord(int wins, int losses) {
        this.wins = checkNotNegative(wins);
        this.losses = checkNotNegative(losses);
    }
    
    public static WinLossRecord of(PitchingStatLine line) {
        return new WinLossRecord(line.get(PitchingStat.WINS), line.get(PitchingStat.LOSSES));
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
    
    public Average getWinPct() {
        return new Average(wins, wins + losses);
    }
    
    public WinLossRecord plus(Decision decision) {
        requireNonNull(decision);
        return (decision == Decision.WIN)
                ? new WinLossRecord(wins + 1, losses)
                : new WinLossRecord(wins, losses + 1);
    }
    
    @Override
    public String toString() {
        return wins + "-" + losses;
    }
}
