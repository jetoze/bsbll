package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Inning implements Comparable<Inning> {
    /**
     * Defines the natural sort order of innings.
     */
    private static final Comparator<Inning> NATURAL_ORDER = 
            Comparator.comparingInt(Inning::getNumber).thenComparing(Inning::getHalf);

    private static final int NUMBER_OF_INNINGS_TO_CACHE = 9;
    private static final EnumMap<Half, Inning[]> CACHE = loadCache(NUMBER_OF_INNINGS_TO_CACHE);
    
    private static EnumMap<Half, Inning[]> loadCache(int numOfInningsToCache) {
        EnumMap<Half, Inning[]> cache = new EnumMap<>(Half.class);
        cache.put(Half.TOP, loadCachedInnings(numOfInningsToCache, Half.TOP));
        cache.put(Half.BOTTOM, loadCachedInnings(numOfInningsToCache, Half.BOTTOM));
        return cache;
    }
    
    private static Inning[] loadCachedInnings(int numOfInningsToCache, Half half) {
        Inning[] cache = new Inning[numOfInningsToCache];
        for (int n = 0; n < numOfInningsToCache; ++n) {
            cache[n] = new Inning(n + 1, half);
        }
        return cache;
    }
    
    public static enum Half { TOP, BOTTOM }
    
    private final int number;
    private final Half half;

    public Inning(int num, Half half) {
        this.number = checkPositive(num);
        this.half = requireNonNull(half);
    }
    
    public static Inning startOfGame() {
        return topOf(1);
    }
    
    public static Inning of(int num, Half half) {
        checkPositive(num);
        requireNonNull(half);
        return (num <= NUMBER_OF_INNINGS_TO_CACHE)
                ? CACHE.get(half)[num - 1]
                : new Inning(num, half);
    }
    
    public static Inning topOf(int num) {
        return of(num, Half.TOP);
    }
    
    public static Inning bottomOf(int num) {
        return of(num, Half.BOTTOM);
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
    public int compareTo(Inning o) {
        return NATURAL_ORDER.compare(this, o);
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
