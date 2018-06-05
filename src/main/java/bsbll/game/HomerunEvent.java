package bsbll.game;

import static tzeth.preconds.MorePreconditions.checkInRange;
import static tzeth.preconds.MorePreconditions.checkPositive;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

/**
 * Data regarding a homerun that was hit during the game.
 */
@Immutable
public final class HomerunEvent extends ExtraBaseHitEvent {
    private final int inning;
    private final int outs;
    private final int runnersOn;
    
    public HomerunEvent(Player batter, Player pitcher, int inning, int outs, int runnersOn) {
        super(Outcome.HOMERUN, batter, pitcher);
        this.inning = checkPositive(inning);
        this.outs = checkInRange(outs, 0, 2);
        this.runnersOn = checkInRange(runnersOn, 0, 3);
    }

    public int getInning() {
        return inning;
    }
    
    public int getOuts() {
        return outs;
    }

    public int getRunnersOn() {
        return runnersOn;
    }
}
