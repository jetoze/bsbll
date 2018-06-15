package bsbll.game.params;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;
import static tzeth.preconds.MorePreconditions.checkOneOf;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import bsbll.game.play.EventType;

/**
 * The key to the OutAdvanceDistribution data. The key consists on the location
 * of the out (infield/outfield), and the number of outs.
 * <p>
 * Including the number of outs in the key is crucial. For example, in the 1925
 * play-by-play data, on an infield out with a runner on first, the by far most
 * common Advances is the no-advance, with 2371 occurrences. The case of the
 * runner on first advancing to second comes in at 1411. This makes no sense,
 * until we realize that many of the no-advance cases are when the last out of
 * the inning was made on an infield out.
 */
@Immutable
public final class OutAdvanceKey {
    private final EventType type;
    private final OutLocation location;
    private final int outs;
    
    public OutAdvanceKey(EventType type, OutLocation location, int outs) {
        this.type = checkOneOf(type, EventType.OUT, EventType.FIELDERS_CHOICE);
        this.location = requireNonNull(location);
        this.outs = checkInRange(outs, 0, 2);
    }

    public static OutAdvanceKey of(EventType type, OutLocation location, int outs) {
        return new OutAdvanceKey(type, location, outs);
    }
    
    public OutLocation getLocation() {
        return location;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, location, outs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof OutAdvanceKey) {
            OutAdvanceKey that = (OutAdvanceKey) obj;
            return this.type == that.type && this.location == that.location && this.outs == that.outs;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %d %s", type, location, outs, (outs == 1 ? "out" : "outs"));
    }
}
