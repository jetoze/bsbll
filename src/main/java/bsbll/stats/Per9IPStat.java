package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.text.DecimalFormat;

import javax.annotation.Nullable;

/**
 * A per-9-innings-pitched based stat, such as ERA, or SO/9IP.
 */
public final class Per9IPStat implements Comparable<Per9IPStat> {
    private final int value;
    private final int outs;

    public Per9IPStat(int value, InningsPitched ip) {
        this(value, ip.toOuts());
    }
    
    public Per9IPStat(int value, int outs) {
        this.value = checkNotNegative(value);
        this.outs = checkNotNegative(outs);
    }
    
    public double asDouble() {
        return outs > 0
                ? (27.0 * value) / outs
                : Double.MAX_VALUE;
    }

    @Override
    public int compareTo(Per9IPStat o) {
        return Double.compare(this.asDouble(), o.asDouble());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        return ((obj instanceof Per9IPStat) && (Double.compare(this.asDouble(), ((Per9IPStat) obj).asDouble()) == 0));
    }

    @Override
    public int hashCode() {
        return Double.hashCode(asDouble());
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
