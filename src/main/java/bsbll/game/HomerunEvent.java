package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
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
    
    public HomerunEvent(Player batter, Player pitcher, int seasonTotal, int inning, int outs, int runnersOn) {
        super(Outcome.HOMERUN, batter, pitcher, seasonTotal);
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
    
    public static Builder builder(Player batter, Player pitcher) {
        return new Builder(batter, pitcher);
    }
    
    
    public static final class Builder {
        private final Player batter;
        private final Player pitcher;
        private int inning;
        private int seasonTotal = 1;
        private int outs;
        private int runnersOn;
        
        public Builder(Player batter, Player pitcher) {
            this.batter = requireNonNull(batter);
            this.pitcher = requireNonNull(pitcher);
        }
        
        public Builder withSeasonTotal(int total) {
            checkArgument(total >= 1);
            this.seasonTotal = total;
            return this;
        }

        public Builder inInning(int inning) {
            this.inning = checkPositive(inning);
            return this;
        }
        
        public Builder withOuts(int outs) {
            this.outs = checkInRange(outs, 0, 2);
            return this;
        }
        
        public Builder withRunnersOn(int runnersOn) {
            this.runnersOn = checkInRange(runnersOn, 0, 3);
            return this;
        }
        
        public HomerunEvent build() {
            return new HomerunEvent(batter, pitcher, seasonTotal, inning, outs, runnersOn);
        }
    }
}
