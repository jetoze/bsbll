package bsbll.stats;

import static com.google.common.base.Preconditions.checkArgument;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.text.DecimalFormat;

public final class Average implements Comparable<Average> {
    private final int numerator;
    private final int denominator;
    
    public Average(int numerator, int denominator) {
        this.numerator = checkNotNegative(numerator);
        this.denominator = checkNotNegative(denominator);
        checkArgument(numerator <= denominator);
    }

    public double asDouble() {
        return denominator > 0
                ? 1.0 * numerator / denominator
                : -1.0;
    }

    @Override
    public int compareTo(Average o) {
        return Double.compare(this.asDouble(), o.asDouble());
    }
    
    @Override
    public String toString() {
        if (denominator == 0) {
            return "----";
        } else {
            DecimalFormat fmt = new DecimalFormat(".###");
            return fmt.format(asDouble());
        }
    }
    
}
