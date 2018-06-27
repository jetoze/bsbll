package bsbll.game.event;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import javax.annotation.concurrent.Immutable;

import bsbll.game.Inning;
import bsbll.player.Player;

/**
 * Data regarding a homerun that was hit during the game.
 */
@Immutable
public final class HomerunEvent extends BattingEvent {
    private final int outs;
    private final int runnersOn;
    
    public HomerunEvent(Inning inning, Player batter, Player pitcher, int seasonTotal, int outs, int runnersOn) {
        super(inning, batter, pitcher, seasonTotal);
        this.outs = checkInRange(outs, 0, 2);
        this.runnersOn = checkInRange(runnersOn, 0, 3);
    }
    
    public int getOuts() {
        return outs;
    }

    public int getRunnersOn() {
        return runnersOn;
    }
    
    public static Builder builder(Inning inning, Player batter, Player pitcher) {
        return new Builder(inning, batter, pitcher);
    }
    
    
    public static final class Builder {
        private final Player batter;
        private final Player pitcher;
        private final Inning inning;
        private int seasonTotal = 1;
        private int outs;
        private int runnersOn;
        
        public Builder(Inning inning, Player batter, Player pitcher) {
            this.inning = requireNonNull(inning);
            this.batter = requireNonNull(batter);
            this.pitcher = requireNonNull(pitcher);
        }
        
        public Builder withSeasonTotal(int total) {
            checkArgument(total >= 1);
            this.seasonTotal = total;
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
            return new HomerunEvent(inning, batter, pitcher, seasonTotal, outs, runnersOn);
        }
    }
}
