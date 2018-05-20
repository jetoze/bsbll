package bsbll;

import static tzeth.preconds.MorePreconditions.checkInRange;

import javax.annotation.Nullable;

public final class Year {
    private final int year;
    
    private Year(int year) {
        this.year = checkInRange(year, 1871, 2017);
    }

    public static Year of(int year) {
        return new Year(year);
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.year);
    }
    
    @Override
    public boolean equals(@Nullable Object o) {
        return (o == this) || ((o instanceof Year) && this.year == ((Year) o).year);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(this.year);
    }
}
