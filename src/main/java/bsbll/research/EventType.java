package bsbll.research;

/**
 * Represents the main outcome of a pitcher-batter matchup.
 */
public enum EventType {
    SINGLE,
    DOUBLE,
    TRIPLE,
    HOMERUN,
    STRIKEOUT,
    WALK,
    HIT_BY_PITCH,
    /**
     * Fielder's choice, also includes force outs.
     */
    FIELDERS_CHOICE,
    PASSED_BALL,
    WILD_PITCH,
    REACHED_ON_ERROR,
    BALK,
    STOLEN_BASE,
    CAUGHT_STEALING,
    PICKED_OFF,
    /**
     * The special case where a batters life is prolonged by an error on a foul
     * fly. Note that the batter does not reach base, and runners (if any) may
     * not advance on this event.
     */
    ERROR_ON_FOUL_FLY,
    INTERFERENCE,
    OTHER_ADVANCE,
    DEFENSIVE_INDIFFERENCE,
    NO_PLAY,
    OUT;
    
    public boolean isError() {
        return this == EventType.REACHED_ON_ERROR || this == ERROR_ON_FOUL_FLY;
    }
}
