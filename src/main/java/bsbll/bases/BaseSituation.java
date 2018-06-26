package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import bsbll.game.BaseRunner;
import tzeth.collections.ImCollectors;

@Immutable
public final class BaseSituation {
    // XXX: In some situations, such as when testing, it is cumbersome to have to
    // create actual Players to move around the bases. All that we really need
    // in those situations is some kind of arbitrary marker, like a string.
    // I've been considering making this class generic, with a type parameter for
    // the type of objects that occupy the bases, but have decided against it 
    // since it adds extra complexity.
    private final ImmutableSortedMap<Base, BaseRunner> bases;
    
    public static BaseSituation empty() {
        return new BaseSituation(ImmutableMap.of());
    }
    
    public BaseSituation(@Nullable BaseRunner onFirst, @Nullable BaseRunner onSecond, @Nullable BaseRunner onThird) {
        ImmutableSortedMap.Builder<Base, BaseRunner> builder = ImmutableSortedMap.naturalOrder();
        if (onFirst != null) {
            builder.put(Base.FIRST, onFirst);
        }
        if (onSecond != null) {
            builder.put(Base.SECOND, onSecond);
        }
        if (onThird != null) {
            builder.put(Base.THIRD, onThird);
        }
        this.bases = builder.build();
    }
    
    public BaseSituation(Map<Base, BaseRunner> bases) {
        checkArgument(!bases.containsKey(Base.HOME));
        this.bases = ImmutableSortedMap.copyOf(bases);
    }

    public boolean areEmpty() {
        return this.bases.isEmpty();
    }
    
    public boolean areLoaded() {
        return this.bases.size() == 3;
    }
    
    public int getNumberOfRunners() {
        return this.bases.size();
    }
    
    public boolean isOccupied(Base base) {
        requireNonNull(base);
        return this.bases.containsKey(base);
    }
    
    public OccupiedBases getOccupiedBases() {
        return areEmpty()
                ? OccupiedBases.NONE
                : OccupiedBases.of(this.bases.keySet());
    }
    
    public ResultOfAdvance advanceRunners(BaseRunner batter, Advances advances) {
        if (advances.isEmpty()) {
            return new ResultOfAdvance(this, ImmutableList.of());
        }
        BaseSituation newSituation = createNewSituation(batter, advances);
        ImmutableList<BaseRunner> runs = advances.getRunnersThatScored()
                .map(Advance::from)
                .map(f -> getPlayerOnBase(batter, f))
                .collect(ImCollectors.toList());
        return new ResultOfAdvance(newSituation, runs);
    }
    
    private BaseSituation createNewSituation(BaseRunner batter, Advances advances) {
        Map<Base, BaseRunner> newSituation = new HashMap<>(this.bases);
        // This implementation relies on the guaranteed iteration order of 
        // Advances.iterator().
        for (Advance a : advances) {
            BaseRunner p;
            if (a.from() == Base.HOME) {
                p = batter;
            } else {
                p = newSituation.remove(a.from());
                if (p == null) {
                    throw new InvalidBaseSitutationException("No runner on base " + a.from());
                }
            }
            if (a.isOut() || !a.to().isOccupiable()) {
                continue;
            }
            if (newSituation.containsKey(a.to())) {
                throw new InvalidBaseSitutationException("Two runners on base " + a.to());
            }
            newSituation.put(a.to(), p);
        }
        return new BaseSituation(newSituation);
    }

    private BaseRunner getPlayerOnBase(BaseRunner batter, Base base) {
        if (base == Base.HOME) {
            return batter;
        }
        if (isOccupied(base)) {
            return this.bases.get(base);
        } else {
            throw new InvalidBaseSitutationException("No runner on base " + base);
        }
    }
    
    public Map<Base, BaseRunner> toMap() {
        return new HashMap<>(this.bases);
    }

    @Override
    public int hashCode() {
        return this.bases.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) || 
                ((obj instanceof BaseSituation) && this.bases.equals(((BaseSituation) obj).bases));
    }

    @Override
    public String toString() {
        return this.bases.toString();
    }
    
    
    public static final class ResultOfAdvance {
        private final BaseSituation newSituation;
        private final ImmutableList<BaseRunner> runs;
        
        public ResultOfAdvance(Map<Base, BaseRunner> newSituation, List<BaseRunner> runs) {
            this(new BaseSituation(newSituation), runs);
        }
        
        /**
         * 
         * @param newSituation
         *            the new base situation
         * @param runs
         *            the runners that scored on the play, in the order in which
         *            they scored.
         */
        public ResultOfAdvance(BaseSituation newSituation, List<BaseRunner> runs) {
            this.newSituation = requireNonNull(newSituation);
            this.runs = ImmutableList.copyOf(runs);
        }
        
        public BaseSituation getNewSituation() {
            return newSituation;
        }
        
        /**
         * Returns the runners that scored on the play, in the order that they scored.
         */
        public ImmutableList<BaseRunner> getRunnersThatScored() {
            return runs;
        }
        
        public int getNumberOfRuns() {
            return runs.size();
        }
        
        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ResultOfAdvance) {
                ResultOfAdvance that = (ResultOfAdvance) obj;
                return this.newSituation.equals(that.newSituation) && this.runs.equals(that.runs);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(newSituation, runs);
        }

        @Override
        public String toString() {
            return newSituation + " " + runs;
        }
    }
}
