package bsbll.game.play;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import bsbll.bases.Advances;
import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.BaseRunner;
import bsbll.player.Player;

@Immutable
public final class Play {
    private final Player batter;
    private final Player pitcher;
    private final PlayOutcome outcome;

    public Play(Player batter, Player pitcher, PlayOutcome outcome) {
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
        this.outcome = requireNonNull(outcome);
    }

    public Player getBatter() {
        return batter;
    }

    public Player getPitcher() {
        return pitcher;
    }

    public PlayOutcome getOutcome() {
        return outcome;
    }
    
    public boolean isNoPlay() {
        return outcome.isNoPlay();
    }
    
    public Advances getAdvances() {
        return outcome.getAdvances();
    }
    
    public int getNumberOfRuns() {
        return outcome.getNumberOfRuns();
    }
    
    public int getNumberOfOuts() {
        return outcome.getNumberOfOuts();
    }
    
    public int getNumberOfErrors() {
        return outcome.getNumberOfErrors();
    }
    
    public boolean isErrorOrPassedBall() {
        return outcome.isErrorOrPassedBall();
    }
    
    public ResultOfAdvance advanceRunners(BaseSituation bases) {
        return bases.advanceRunners(new BaseRunner(batter, pitcher), outcome.getAdvances());
    }
    
    @Override
    public String toString() {
        return String.format("%s vs %s -- %s", batter, pitcher, outcome);
    }

}
