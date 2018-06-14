package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.EnumSet;

import bsbll.game.play.EventType;
import bsbll.matchup.MatchupRunner.Outcome;

public enum BaseHit {
    // TODO: Should I be in this package?
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    HOMERUN(4);
    
    private final int value;
    
    private BaseHit(int value) {
        this.value = value;
    }
    
    public int value() {
        return value;
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
