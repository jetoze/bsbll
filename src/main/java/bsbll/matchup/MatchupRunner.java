package bsbll.matchup;

import bsbll.player.Player;

/**
 * The matchup between a batter and a pitcher.
 */
public interface MatchupRunner {
    /**
     * Runs the matchup and returns the basic outcome.
     * <p>
     * In game play, the returned outcome may be modified by subsequent processing,
     * e.g. Outcome.OUT may end up being transmutated into an error.
     */
    Outcome run(Player batter, Player pitcher);

    /**
     * Represents the basic (initial) outcome of a batter-pitcher matchup. This
     * only covers states that are given by the batter's and pitcher's
     * respective skills, such as base hits and strikeouts. Detailed outcome
     * states, including things like errors and base running events, are not
     * included, and will be added on in a later state of the game processing of
     * a matchup.
     */
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