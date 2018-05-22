package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import javax.annotation.Nullable;

public final class InningsPitched implements Comparable<InningsPitched> {
    private final int outs;
    
    public InningsPitched(int outs) {
        this.outs = checkNotNegative(outs);
    }
    
    public static InningsPitched fromOuts(int outs) {
        return new InningsPitched(outs);
    }

    public int toOuts() {
        return outs;
    }

    @Override
    public int compareTo(InningsPitched o) {
        return Integer.compare(this.outs, o.outs);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(outs);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) ||
                ((obj instanceof InningsPitched) && (this.outs == (((InningsPitched) obj).outs)));
    }

    @Override
    public String toString() {
        int whole = outs / 3;
        int parts = outs % 3;
        return whole + "." + parts;
    }
    
}
