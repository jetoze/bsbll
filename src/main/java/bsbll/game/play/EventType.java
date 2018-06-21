package bsbll.game.play;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import bsbll.bases.Advance;
import bsbll.bases.Advance.Outcome;
import bsbll.bases.Base;

/**
 * Represents the detailed outcome of a pitcher-batter matchup.
 */
public enum EventType { // TODO: Rename me now that I'm used in non-retrosheet contexts.
    
    // TODO: Some of these events can happen before the batter-pitcher matchup runs,
    // e.g. PICKED_OFF. Others technically happens during the matchup, but is best
    // simulated separately before running the matchup, e.g. BALK and ERROR_ON_FOUL_FLY.
    
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
    /**
     * A base runner advance that is not covered by one of the other types.
     * <p>
     * This EventType will only appear when parsing retrosheet play-by-play files. It will
     * not appear in normal game play.
     */
    OTHER_ADVANCE,
    DEFENSIVE_INDIFFERENCE,
    /**
     * Used as a marker when substitutions are made.
     */
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
    
    public boolean isBatterOut() {
        return (this == OUT) || (this == STRIKEOUT);
    }
}
