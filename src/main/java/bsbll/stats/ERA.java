package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.text.DecimalFormat;

import javax.annotation.Nullable;

public final class ERA implements Comparable<ERA> {
    private final int earnedRuns;
    private final int outs;

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
