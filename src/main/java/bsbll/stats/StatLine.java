package bsbll.stats;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.Stat.PrimitiveStat;

/**
 * A collection of one or more {@link PrimitiveStat}s and their values.
 * <p>
 * A StatLine should be immutable. New StatLines are derived by adding new
 * values to the existing stats, via one of the several add-methods.
 *
 * @param <S>
 *            the type of PrimitiveStat contained in the StatLine (batting or
 *            pitching)
 * @param <T>
 *            the StatLine type itself. The recursive type parameter along with
 *            the {@link #newInstance(Map)} method is an example of the
 *            simulated self-type idiom (Effective Java, 3rd edition, Item 2).
 */
public abstract class StatLine<S extends PrimitiveStat, T extends StatLine<S, T>> {
    private final ImmutableMap<S, Integer> values;
    
    /**
     * Creates a StatLine that returns 0 (zero) for all stats.
     */
    protected StatLine() {
        this.values = ImmutableMap.of();
    }

    /**
     * Creates a StatLine of the following values.
     */
    protected StatLine(Map<S, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
    }

    public final T add(S stat, int value) {
        return add(new PrimitiveStatValue<>(stat, value));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2) {
        return add(new PrimitiveStatValue<>(stat1, value1), 
                new PrimitiveStatValue<>(stat2, value2));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3) {
        return add(new PrimitiveStatValue<>(stat1, value1), 
                new PrimitiveStatValue<>(stat2, value2), 
                new PrimitiveStatValue<>(stat3, value3));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3,
            S stat4, int value4) {
        return add(new PrimitiveStatValue<>(stat1, value1), 
                new PrimitiveStatValue<>(stat2, value2), 
                new PrimitiveStatValue<>(stat3, value3), 
                new PrimitiveStatValue<>(stat4, value4));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3,
            S stat4, int value4, S stat5, int value5) {
        return add(new PrimitiveStatValue<>(stat1, value1), 
                new PrimitiveStatValue<>(stat2, value2), 
                new PrimitiveStatValue<>(stat3, value3), 
                new PrimitiveStatValue<>(stat4, value4),
                new PrimitiveStatValue<>(stat5, value5));
    }
    
    @SafeVarargs
    private final T add(PrimitiveStatValue<S>... newValues) {
        Map<S, Integer> tmp = new HashMap<>(this.values);
        for (PrimitiveStatValue<S> sv : newValues) {
            tmp.merge(sv.stat, sv.value, (p, q) -> p + q);
        }
        return newInstance(tmp);
    }
    
    /**
     * Adds another StatLine to this StatLine, and returns the result. This
     * StatLine is not modified.
     */
    public final T add(T o) {
        Map<S, Integer> tmp = new HashMap<>(this.values);
        o.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return newInstance(tmp);
    }
    
    protected final void forEach(BiConsumer<S, Integer> c) {
        this.values.forEach(c);
    }
    
    // HACK: This method is conceptually a factory method, and should be static.
    // Since static methods can't be abstract, we instead make it an instance
    // method. It's only used when creating a new StatLine instance from an
    // an existing one, by updating one or more of the existing StatLine's values,
    // so it works out, but it is super ugly.
    // One alternative would be to instead use reflection to lookup the corresponding
    // constructor, but that feels even worse.
    /**
     * Creates a new StatLine of the same type as {@code this} one, but with the
     * given values.
     */
    protected abstract T newInstance(Map<S, Integer> values);

    /**
     * Returns the value of the given primitive stat.
     * <p>
     * This method is used by the individual {@code Stat} implementations
     * themselves. It can be used to get the value of individual primitive
     * stats, as well as composing values for derived stats. As an example from
     * batting: Batting Average is composed of the primitive stat HITS and the
     * derived stat AT BATS. AT BATS is in turn derived from the primitive stats
     * PLATE APPEARANCES, WALKS, HIT BY PITCHES, SACRIFICE HITS, and SACRIFICE
     * FLIES (and INTERFERENCE, when we have it).
     */
    protected final int getPrimitiveStat(S stat) {
        requireNonNull(stat);
        return this.values.getOrDefault(stat, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            return this.values.equals(((StatLine<?, ?>) obj).values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        TreeMap<S, Integer> sorted = new TreeMap<>(this.values);
        return sorted.toString();
    }
    
    
    
    private static final class PrimitiveStatValue<S extends PrimitiveStat> {
        public final S stat;
        public final int value;
        
        public PrimitiveStatValue(S stat, int value) {
            this.stat = requireNonNull(stat);
            this.value = checkNotNegative(value);
        }
    }
}
