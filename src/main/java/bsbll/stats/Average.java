package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.text.DecimalFormat;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.math.IntMath;

/**
 * Represents an average, such as a Batting Average or Slugging Average.
 */
@Immutable
public final class Average implements Comparable<Average> {
    private final int numerator;
    private final int denominator;
    private final boolean includeLeadingZero;
    
    public Average(int numerator, int denominator) {
        this(numerator, denominator, false);
    }
    
    public Average(int numerator, int denominator, boolean includeLeadingZero) {
        this.numerator = checkNotNegative(numerator);
        this.denominator = checkNotNegative(denominator);
        this.includeLeadingZero = includeLeadingZero;
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
    public int hashCode() {
        return Double.hashCode(asDouble());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Average) {
            return Double.valueOf(this.asDouble()).equals(((Average) obj).asDouble());
        }
        return false;
    }

    @Override
    public String toString() {
        if (denominator == 0) {
            return "----";
        } else {
            DecimalFormat fmt = includeLeadingZero
                    ? new DecimalFormat("0.000")
                    : new DecimalFormat("#.000");
            return fmt.format(asDouble());
        }
    }
    
    public static Average sumOf(Average a1, Average a2) {
        if (a1.denominator == a2.denominator) {
            return new Average(a1.numerator + a2.numerator, a1.denominator);
        } else {
            int gcd = IntMath.gcd(a1.denominator, a2.denominator);
            int lcm = (a1.denominator / gcd) * a2.denominator;
            int n1 = a1.numerator * (lcm / a1.denominator);
            int n2 = a2.numerator * (lcm / a2.denominator);
            return new Average(n1 + n2, lcm);
        }
    }
    
}
