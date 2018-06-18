package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.EnumSet;
import java.util.Optional;

import javax.annotation.Nullable;

import bsbll.game.play.EventType;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

public enum BaseHit {
    // TODO: Should I be in this package?
    SINGLE(1, EventType.SINGLE, null, null),
    DOUBLE(2, EventType.DOUBLE, PrimitiveBattingStat.DOUBLES, null),
    TRIPLE(3, EventType.TRIPLE, PrimitiveBattingStat.TRIPLES, null),
    HOMERUN(4, EventType.HOMERUN, PrimitiveBattingStat.HOMERUNS, PrimitivePitchingStat.HOMERUNS);
    
    private final int value;
    private final EventType eventType;
    @Nullable
    private final PrimitiveBattingStat battingStat;
    @Nullable
    private final PrimitivePitchingStat pitchingStat;
    
    private BaseHit(int value, 
                    EventType eventType,
                    @Nullable PrimitiveBattingStat battingStat,
                    @Nullable PrimitivePitchingStat pitchingStat) {
        this.value = value;
        this.eventType = eventType;
        this.battingStat = battingStat;
        this.pitchingStat = pitchingStat;
    }
    
    public int value() {
        return value;
    }
    
    public EventType toEventType() {
        return eventType;
    }
    
    public Optional<PrimitiveBattingStat> getBattingStat() {
        return Optional.ofNullable(battingStat);
    }
    
    public Optional<PrimitivePitchingStat> getPitchingStat() {
        return Optional.ofNullable(pitchingStat);
    }
    
    public static EnumSet<BaseHit> otherThanHomerun() {
        return EnumSet.of(SINGLE, DOUBLE, TRIPLE);
    }
    
    public static BaseHit fromMatchupOutcome(Outcome o) {
        checkArgument(o.isHit());
        switch (o) {
        case SINGLE:
            return BaseHit.SINGLE;
        case DOUBLE:
            return BaseHit.DOUBLE;
        case TRIPLE:
            return BaseHit.TRIPLE;
        case HOMERUN:
            return BaseHit.HOMERUN;
        default:
            throw new AssertionError("Unexpected hit type: " + o);
        }
    }
    
    public static BaseHit fromEventType(EventType o) {
        checkArgument(o.isHit());
        switch (o) {
        case SINGLE:
            return BaseHit.SINGLE;
        case DOUBLE:
            return BaseHit.DOUBLE;
        case TRIPLE:
            return BaseHit.TRIPLE;
        case HOMERUN:
            return BaseHit.HOMERUN;
        default:
            throw new AssertionError("Unexpected event type: " + o);
        }
    }
}
