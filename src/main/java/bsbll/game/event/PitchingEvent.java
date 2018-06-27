package bsbll.game.event;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import bsbll.game.Inning;
import bsbll.player.Player;

public abstract class PitchingEvent implements GameEvent {
    private final Inning inning;
    private final Player pitcher;
    private final int seasonTotal;

    public PitchingEvent(Inning inning, Player pitcher, int seasonTotal) {
        this.inning = requireNonNull(inning);
        this.pitcher = requireNonNull(pitcher);
        this.seasonTotal = checkNotNegative(seasonTotal);
    }

    @Override
    public final Inning getInning() {
        return inning;
    }

    public final Player getPitcher() {
        return pitcher;
    }

    public final int getSeasonTotal() {
        return seasonTotal;
    }
    
    @Override
    public String toString() {
        return String.format("%s pitcher: %s", getClass().getSimpleName(), pitcher);
    }
}
