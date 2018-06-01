package bsbll.stats;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.Stat.BasicStat;
import bsbll.stats.Stat.BasicStatValue;

public abstract class StatLine<S extends BasicStat<S>, T extends StatLine<S, T>> {
    private final ImmutableMap<S, Integer> values;
    
    protected StatLine() {
        this.values = ImmutableMap.of();
    }
    
    protected StatLine(Map<S, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
    }

    public final T add(S stat, int value) {
        return add(Collections.singleton(stat.withValue(value)));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2) {
        return add(Arrays.asList(stat1.withValue(value1), stat2.withValue(value2)));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3) {
        return add(Arrays.asList(stat1.withValue(value1), stat2.withValue(value2), 
                stat3.withValue(value3)));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3,
            S stat4, int value4) {
        return add(Arrays.asList(stat1.withValue(value1), stat2.withValue(value2), 
                stat3.withValue(value3), stat4.withValue(value4)));
    }
    
    public final T add(S stat1, int value1, S stat2, int value2, S stat3, int value3,
            S stat4, int value4, S stat5, int value5) {
        return add(Arrays.asList(stat1.withValue(value1), stat2.withValue(value2), 
                stat3.withValue(value3), stat4.withValue(value4), stat5.withValue(value5)));
    }
    
    public final T add(Collection<BasicStatValue<S>> newValues) {
        Map<S, Integer> tmp = new HashMap<>(this.values);
        newValues.forEach(v -> tmp.merge(v.getStat(), v.getValue(), (p, q) -> p + q));
        return newInstance(tmp);
    }
    
    public final T add(T o) {
        Map<S, Integer> tmp = new HashMap<>(this.values);
        o.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return newInstance(tmp);
    }
    
    protected final void forEach(BiConsumer<S, Integer> c) {
        this.values.forEach(c);
    }
    
    protected abstract T newInstance(Map<S, Integer> values);

    protected final int getBasicStat(S stat) {
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
}
