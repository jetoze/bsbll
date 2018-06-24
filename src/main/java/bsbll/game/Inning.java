package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Inning { // TODO: Implement Comparable<Inning>
    
    public static enum Half { TOP, BOTTOM }
    
    private final int number;
    private final Half half;

    public Inning(int num, Half half) {
        this.number = checkPositive(num);
        this.half = requireNonNull(half);
    }
    
    public static Inning startOfGame() {
        return new Inning(1, Half.TOP);
    }
    
    public static Inning topOf(int num) {
        // TODO: Cache common instances, or use pre-defined constants.
        return new Inning(num, Half.TOP);
    }
    
    public static Inning bottomOf(int num) {
        // TODO: See topOf.
        return new Inning(num, Half.BOTTOM);
    }

    public int getNumber() {
        return number;
    }
    
    public Half getHalf() {
        return half;
    }
    
    public boolean isTop() {
        return half == Half.TOP;
    }
    
    public boolean isBottom() {
        return half == Half.BOTTOM;
    }

    public Inning next() {
        return isTop()
                ? new Inning(number, Half.BOTTOM)
                : new Inning(number + 1, Half.TOP);
    }
    
    public String getNumberAsString() {
        // TODO: Move to common utility
        switch (number) {
        case 1:
            return "1st";
        case 2:
            return "2nd";
        case 3:
            return "3rd";
        default:
            return number + "th";
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Inning) {
            Inning that = (Inning) obj;
            return this.number == that.number && this.half == that.half;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(number, half);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(isTop() ? "Top" : "Bottom").append(" of ").append(getNumberAsString());
        return sb.toString();
    }
}
