package bsbll.card;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.die.Die;
import bsbll.die.DieFactory;
import tzeth.collections.ImCollectors;

/**
 * Represents the probability of an event, such as a batter hitting a homerun.
 */
@Immutable
public final class Probability implements Comparable<Probability> {
    private static final int DENOMINATOR = 10_000;
    
    public static final Probability ZERO = new Probability(0);
    public static final Probability COMPLETE = new Probability(DENOMINATOR);
    
    /*
     * The idea of this abstraction is to avoid having to work with doubles and instead
     * use ints throughout, and at the same time hide the implementation detail of
     * what denominator we're using. Let's see how it works out. 
     */
    
    private final int value;
    
    private Probability(int value) {
        checkArgument(value >= 0 && value <= DENOMINATOR, 
                "value must be in the range [0, %s], but was %s", DENOMINATOR, value);
        this.value = value;
    }
    
    private Probability(double value) {
        checkArgument(value >= 0 && value <= 1,
                "value must be in the range [0, 1], but was %s", value);
        this.value = (int) (value * DENOMINATOR);
    }

    public static Probability of(int events, int total) {
        checkArgument(events >= 0);
        checkArgument(total > 0);
        checkArgument(events <= total);
        int value = (int) Math.round((((1.0 * events) / total) * DENOMINATOR));
        return new Probability(value);
    }
    
    public static Probability of(double value) {
        return new Probability(value);
    }

    public boolean test(DieFactory dieFactory) {
        Die die = dieFactory.getDie(DENOMINATOR);
        int roll = die.roll();
        return roll <= this.value;
    }
    
    public int apply(int sampleSize) {
        return (int) Math.round(asDouble() * sampleSize);
    }

    public boolean isZero() {
        return this.value == 0;
    }
    
    public boolean isCertain() {
        return this.value == DENOMINATOR;
    }
    
    public boolean canAdd(Probability other) {
        return (this.value + other.value) <= DENOMINATOR;
    }
    
    public Probability add(Probability other) {
        checkArgument(canAdd(other));
        return new Probability(this.value + other.value);
    }
    
    public Probability subtract(Probability other) {
        int newValue = this.value - other.value;
        checkArgument(newValue >= 0);
        return new Probability(newValue);
    }

    public static Probability complementOf(Probability... ps) {
        return complementOf(Arrays.asList(ps));
    }

    public static Probability complementOf(Collection<Probability> ps) {
        checkArgument(ps.size() > 0);
        int total = ps.stream()
                .mapToInt(p -> p.value)
                .sum();
        checkArgument(total <= DENOMINATOR, "The given probabilities already add up to greater than 100%");
        return (total == DENOMINATOR)
                ? ZERO
                : (total == 0)
                    ? COMPLETE
                    : new Probability(DENOMINATOR - total);
    }
    
    public static ImmutableList<Probability> normalize(Probability first, Probability second, 
            Probability... rest) {
        requireNonNull(first);
        requireNonNull(second);
        List<Probability> ps = new ArrayList<>();
        ps.add(first);
        ps.add(second);
        Collections.addAll(ps, rest);
        return normalize(ps);
    }
    
    public static ImmutableList<Probability> normalize(Collection<Probability> ps) {
        checkArgument(ps.size() >= 2);
        int total = ps.stream()
                .mapToInt(p -> p.value)
                .sum();
        return ps.stream()
                .map(p -> Probability.of(p.value, total))
                .collect(ImCollectors.toList());
    }
    
    
    public static Probability log5(Probability batter,
                                   Probability pitcher,
                                   Probability league) {
        // See https://sabr.org/research/matchup-probabilities-major-league-baseball#footnote2_8f5byka
        // The formula breaks down (division by zero) if the league probability is either 0 or 1.
        // TODO: How would we go about unit testing this?
        checkArgument(!league.isZero() && !league.isCertain(),
                "p_league must represent a value in the range ]0, 1[, but was " + league.asDouble());
        double x = batter.asDouble();
        double y = pitcher.asDouble();
        double z = league.asDouble();
        double numerator = x * y / z;
        double denominator1 = numerator;
        double denominator2 = (1 - x) * (1 - y) / (1 - z);
        double result = numerator / (denominator1 + denominator2);
        return new Probability(result);
    }
    
    private double asDouble() {
        return 1.0 * value / DENOMINATOR;
    }

    @Override
    public int compareTo(Probability o) {
        return Integer.compare(this.value, o.value);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) || 
                ((obj instanceof Probability) && (this.value == ((Probability) obj).value));
    }

    @Override
    public String toString() {
        return value + " / " + DENOMINATOR;
    }
    
}
