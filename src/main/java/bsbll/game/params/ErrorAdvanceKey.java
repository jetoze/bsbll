package bsbll.game.params;

import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import bsbll.game.play.EventType;

@Immutable
public final class ErrorAdvanceKey {
    private final EventType type;
    private final int numberOfErrors;

    public ErrorAdvanceKey(EventType type, int numberOfErrors) {
        this.type = ErrorSupport.requireSupported(type);
        this.numberOfErrors = checkPositive(numberOfErrors);
    }
    
    public static ErrorAdvanceKey of(EventType type, int numberOfErrors) {
        return new ErrorAdvanceKey(type, numberOfErrors);
    }
    
    int getNumberOfErrors() {
        return numberOfErrors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, numberOfErrors);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ErrorAdvanceKey) {
            ErrorAdvanceKey that = (ErrorAdvanceKey) obj;
            return (this.type == that.type) && (this.numberOfErrors == that.numberOfErrors);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", type, numberOfErrors);
    }
}
