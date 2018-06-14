package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.EnumSet;

import bsbll.game.play.EventType;
import bsbll.matchup.MatchupRunner.Outcome;

public enum BaseHit {
    // TODO: Should I be in this package?
    SINGLE(1, EventType.SINGLE),
    DOUBLE(2, EventType.DOUBLE),
    TRIPLE(3, EventType.TRIPLE),
    HOMERUN(4, EventType.HOMERUN);
    
    private final int value;
    private final EventType eventType;
    
    private BaseHit(int value, EventType eventType) {
        this.value = value;
        this.eventType = eventType;
    }
    
    public int value() {
        return value;
    }
    
    public EventType toEventType() {
        return eventType;
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
