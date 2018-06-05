package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

public abstract class ExtraBaseHitEvent implements GameEvent {
    private final Outcome type;
    private final Player batter;
    private final Player pitcher;
    // TODO: The batter's total.
    
    public ExtraBaseHitEvent(Outcome type, Player batter, Player pitcher) {
        requireNonNull(type);
        checkArgument(type == Outcome.DOUBLE || type == Outcome.TRIPLE || type == Outcome.HOMERUN,
                "Not an extra base hit: " + type);
        this.type = type;
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
    }

    public final Outcome getType() {
        return type;
    }

    public final Player getBatter() {
        return batter;
    }

    public final Player getPitcher() {
        return pitcher;
    }
    
    @Override
    public String toString() {
        return String.format("%s by %s, off of %s", type, batter, pitcher);
    }
}
