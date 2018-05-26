package bsbll.research;

import java.util.Optional;

import javax.annotation.Nullable;

import bsbll.Base;

/**
 * Represents the main outcome of a pitcher-batter matchup.
 */
public enum EventType {
    SINGLE(Base.FIRST),
    DOUBLE(Base.SECOND),
    TRIPLE(Base.THIRD),
    HOMERUN(Base.HOME),
    STRIKEOUT,
    WALK(Base.FIRST),
    HIT_BY_PITCH(Base.FIRST),
    /**
     * Fielder's choice, also includes force outs.
     */
    FIELDERS_CHOICE(Base.FIRST),
    FORCE_OUT(Base.FIRST),
    PASSED_BALL,
    WILD_PITCH,
    REACHED_ON_ERROR(Base.FIRST), // according to the rules of the retrosheet play-by-play file
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
    
    @Nullable
    private final Base impliedBaseForBatter;
    
    private EventType() {
        this(null);
    }
    
    private EventType(@Nullable Base impliedBaseForBatter) {
        this.impliedBaseForBatter = impliedBaseForBatter;
    }
    
    public Optional<Base> getImpliedBaseForBatter() {
        return Optional.ofNullable(this.impliedBaseForBatter);
    }
    
    public boolean isError() {
        return (this == EventType.REACHED_ON_ERROR) || (this == ERROR_ON_FOUL_FLY);
    }
    
    public boolean isOut() {
        switch (this) {
        case OUT:
        case STRIKEOUT:
        case PICKED_OFF:
        case CAUGHT_STEALING:
            return true;
        default:
            return false;
        }
    }
    
    public boolean isBatterOut() {
        return (this == OUT) || (this == STRIKEOUT);
    }
}
