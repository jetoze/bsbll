package bsbll.research;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import bsbll.bases.Advance;
import bsbll.bases.Base;
import bsbll.bases.Advance.Outcome;

/**
 * Represents the main outcome of a pitcher-batter matchup.
 */
public enum EventType {
    SINGLE(Base.FIRST, Outcome.SAFE),
    DOUBLE(Base.SECOND, Outcome.SAFE),
    TRIPLE(Base.THIRD, Outcome.SAFE),
    HOMERUN(Base.HOME, Outcome.SAFE),
    STRIKEOUT,
    WALK(Base.FIRST, Outcome.SAFE),
    HIT_BY_PITCH(Base.FIRST, Outcome.SAFE),
    /**
     * Fielder's choice, also includes force outs.
     */
    FIELDERS_CHOICE(Base.FIRST, Outcome.SAFE),
    FORCE_OUT(Base.FIRST, Outcome.SAFE),
    PASSED_BALL,
    WILD_PITCH,
    REACHED_ON_ERROR(Base.FIRST, Outcome.SAFE_ON_ERROR),
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
    INTERFERENCE(Base.FIRST, Outcome.SAFE_ON_ERROR),
    OTHER_ADVANCE,
    DEFENSIVE_INDIFFERENCE,
    NO_PLAY,
    OUT;
    
    @Nullable
    private final Supplier<Advance> impliedAdvance;
    
    private EventType() {
        impliedAdvance = null;
    }
    
    private EventType(Base impliedAdvanceTo, Outcome impliedAdvanceType) {
        assert impliedAdvanceTo != null;
        assert impliedAdvanceType != null;
        this.impliedAdvance = () -> new Advance(Base.HOME, impliedAdvanceTo, impliedAdvanceType);
    }
    
    public Optional<Advance> getImpliedAdvance() {
        return (impliedAdvance != null)
                ? Optional.ofNullable(impliedAdvance.get())
                : Optional.empty();
    }
    
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
    
    public boolean isError() {
        return (this == EventType.REACHED_ON_ERROR) || (this == ERROR_ON_FOUL_FLY) || (this == INTERFERENCE);
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
