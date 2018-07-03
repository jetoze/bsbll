package bsbll.game.params;

import static tzeth.preconds.MorePreconditions.checkInRange;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import bsbll.game.play.EventType;
import p3.Persister;

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
    
    void store(Persister p) {
        Storage.store(this, p);
    }
    
    static ErrorAdvanceKey restore(Persister p) {
        return Storage.restore(p);
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
    
    
    private static class Storage {
        private static final String TYPE = "Type";
        private static final String ERRORS = "Errors";
        private static final String OUTS = "Outs";
        
        public static void store(ErrorAdvanceKey key, Persister p) {
            p.putString(TYPE, key.type.name())
                .putInt(ERRORS, key.numberOfErrors)
                .putInt(OUTS, key.outs);
        }
        
        public static ErrorAdvanceKey restore(Persister p) {
            return new ErrorAdvanceKey(
                    EventType.valueOf(p.getString(TYPE)), 
                    p.getInt(ERRORS), 
                    p.getInt(OUTS));
        }
    }
}
