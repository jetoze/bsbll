package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.text.DecimalFormat;

import javax.annotation.Nullable;

// TODO: We also need a representation for things like SO/9IP, BB/9IP. In general, 
// something like Per9IPAverage. ERA is just one example of this more general concept.
// One quirk: different stats have different natural sort order. For ERA and BB, lower
// is better. For SO, higher is better. This needs to be accommodated for by whatever
// abstraction we put in place.
//
// One way would be to base it on the accompanying stat (earned run, strikeout, walk).
// This could each be represented by something that has built-int the knowledge if
// a higher number is better or worse than a lower number. Of course, for something like
// strikeouts and walks this property is opposite for batters and pitchers. Tricky.

public final class ERA implements Comparable<ERA> {
    private final int earnedRuns;
    private final int outs;

    public ERA(int earnedRuns, InningsPitched ip) {
        this(earnedRuns, ip.toOuts());
    }
    
    public ERA(int earnedRuns, int outs) {
        this.earnedRuns = checkNotNegative(earnedRuns);
        this.outs = checkNotNegative(outs);
    }
    
    public double asDouble() {
        return outs > 0
                ? (27.0 * earnedRuns) / outs
                : Double.MAX_VALUE;
    }

    @Override
    public int compareTo(ERA o) {
        return Double.compare(this.asDouble(), o.asDouble());
    }

    @Override
    public int hashCode() {
        return Double.hashCode(asDouble());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ERA) {
            return Double.valueOf(this.asDouble()).equals(((ERA) obj).asDouble());
        }
        return false;
    }


    @Override
    public String toString() {
        if (outs == 0) {
            return "----";
        } else {
            DecimalFormat fmt = new DecimalFormat("0.00");
            return fmt.format(asDouble());
        }
    }
}
