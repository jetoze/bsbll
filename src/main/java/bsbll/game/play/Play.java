package bsbll.game.play;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

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
    
    @Override
    public String toString() {
        return String.format("%s vs %s -- %s", batter, pitcher, outcome);
    }

}
