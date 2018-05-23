package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.Nullable;

import bsbll.Base;

public final class Advance {
    private final Base from;
    private final Base to;
    private final Outcome outcome;

    public Advance(Base from, Base to, Outcome outcome) {
        this.from = requireNonNull(from);
        this.to = requireNonNull(to);
        this.outcome = requireNonNull(outcome);
        // Can't advance backwards. But from == to is allowed.
        checkArgument(from.isHome() || from.compareTo(to) <= 0, "Cannot advance backward from %s to %s", from, to);
    }
    
    Advance(Map.Entry<Base, Base> e, Outcome outcome) {
        this(e.getKey(), e.getValue(), outcome);
    }
    
    public static Advance safe(Base from, Base to) {
        return new Advance(from, to, Outcome.SAFE);
    }
    
    public static Advance out(Base from, Base to) {
        return new Advance(from, to, Outcome.OUT);
    }

    public Base from() {
        return this.from;
    }
    
    public Base to() {
        return this.to;
    }
    
    public Outcome outcome() {
        return this.outcome;
    }
    
    public boolean isSafe() {
        return (this.outcome == Outcome.SAFE);
    }
    
    public boolean isOut() {
        return (this.outcome == Outcome.OUT);
    }
    
    public boolean isRun() {
        return isSafe() && (this.to == Base.HOME);
    }
    
    public boolean isAdvancement() {
        return isSafe() && (this.to != this.from);
    }

    @Override
    public int hashCode() {
        return hash(this.from, this.to, this.outcome);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Advance) {
            Advance that = (Advance) obj;
            return (this.from == that.from) && (this.to == that.to) && (this.outcome == that.outcome);
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(this.from, true) + this.outcome.toChar() + toString(this.to, false);
    }
    
    private static String toString(Base base, boolean from) {
        switch (base) {
        case HOME:
            return from
                    ? "B"
                    : "H";
        case FIRST:
            return "1";
        case SECOND:
            return "2";
        case THIRD:
            return "3";
        default:
            throw new AssertionError("Unexpected base: " + base);
        }
    }
    
    
    public static enum Outcome {
        SAFE,
        OUT;
        
        public static Outcome fromChar(char c) {
            switch (c) {
            case '-':
                return SAFE;
            case 'X':
                return OUT;
            default:
                throw new IllegalArgumentException("Invalid char: " + c);
            }
        }
        
        public char toChar() {
            switch (this) {
            case SAFE:
                return '-';
            case OUT:
                return 'X';
            default:
                throw new AssertionError("Unexpected Outcome: " + this);
            }
        }
    }
    
}
