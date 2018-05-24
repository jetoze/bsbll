package bsbll.matchup;

import bsbll.player.Player;

/**
 * The matchup between a batter and a pitcher.
 */
public interface MatchupRunner {
    Outcome run(Player batter, Player pitcher);

    public static enum Outcome {
        SINGLE,
        DOUBLE,
        TRIPLE,
        HOMERUN,
        STRIKEOUT,
        WALK,
        HIT_BY_PITCH,
        OUT;
        
        public boolean isHit() {
            switch (this) {
            case SINGLE:
            case DOUBLE:
            case TRIPLE:
            case HOMERUN:
                return true;
            default:
                return false;
            }
        }
        
        public boolean isOut() {
            return (this == STRIKEOUT) || (this == OUT);
        }
    }
}