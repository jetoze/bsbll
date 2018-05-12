package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.Nullable;

public final class Advance {
    private final Base from;
    private final Base to;

    public Advance(Base from, Base to) {
        this.from = requireNonNull(from);
        this.to = requireNonNull(to);
        // If from == Base.HOME it represents the Batter reaching base.
        // Any base is allowed as destination, including HOME (== homerun).
        // Otherwise we make sure that from and to actually represent an advancement.
        if (from != Base.HOME) {
            checkArgument(to.compareTo(from) > 0, "Cannot advance backwords from %s to %s", from, to);
        }
    }
    
    Advance(Map.Entry<Base, Base> e) {
        this(e.getKey(), e.getValue());
    }

    public Base from() {
        return from;
    }
    
    public Base to() {
        return to;
    }

    @Override
    public int hashCode() {
        return hash(this.from, this.to);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Advance) {
            Advance that = (Advance) obj;
            return (this.from == that.from) && (this.to == that.to);
        }
        return false;
    }

    @Override
    public String toString() {
        return from + " - " + to;
    }
    
}
