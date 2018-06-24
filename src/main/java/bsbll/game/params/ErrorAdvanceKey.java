package bsbll.game.params;

import static tzeth.preconds.MorePreconditions.checkInRange;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import bsbll.game.play.EventType;

@Immutable
public final class ErrorAdvanceKey {
    private final EventType type;
    private final int numberOfErrors;
    private final int outs;

    public ErrorAdvanceKey(EventType type, int numberOfErrors, int outs) {
        this.type = ErrorSupport.requireSupported(type);
        this.numberOfErrors = checkPositive(numberOfErrors);
        this.outs = checkInRange(outs, 0, 2);
    }
    
    public static ErrorAdvanceKey of(EventType type, int numberOfErrors, int outs) {
        return new ErrorAdvanceKey(type, numberOfErrors, outs);
    }
    
    int getNumberOfErrors() {
        return numberOfErrors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, numberOfErrors, outs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ErrorAdvanceKey) {
            ErrorAdvanceKey that = (ErrorAdvanceKey) obj;
            return (this.type == that.type) && (this.numberOfErrors == that.numberOfErrors) &&
                    (this.outs == that.outs);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s [%d error(s)] [%d out(s)]", type, numberOfErrors, outs);
    }
}
