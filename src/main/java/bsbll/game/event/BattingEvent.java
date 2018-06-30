package bsbll.game.event;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import bsbll.game.Inning;
import bsbll.player.Player;

public abstract class BattingEvent implements GameEvent {
    private final Inning inning;
    private final Player batter;
    private final Player pitcher;
    private final int seasonTotal;

    protected BattingEvent(Inning inning, Player batter, Player pitcher, int seasonTotal) {
        this.inning = requireNonNull(inning);
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
        this.seasonTotal = checkNotNegative(seasonTotal);
    }

    @Override
    public Inning getInning() {
        return inning;
    }

    public Player getBatter() {
        return batter;
    }

    public Player getPitcher() {
        return pitcher;
    }

    @Override
    public int getSeasonTotal() {
        return seasonTotal;
    }
    
    @Override
    public String toString() {
        return String.format("%s batter: %s, pitcher: %s", getClass().getSimpleName(), batter, pitcher);
    }
}
