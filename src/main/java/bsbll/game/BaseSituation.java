package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import bsbll.Base;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

@Immutable
public final class BaseSituation {
    // XXX: In some situations, such as when testing, it is cumbersome to have to
    // create actual Players to move around the bases. All that we really need
    // in those situations is some kind of arbitrary marker, like a string.
    // I've been considering making this class generic, with a type parameter for
    // the type of objects that occupy the bases, but have decided against it 
    // since it adds extra complexity.
    private final ImmutableSortedMap<Base, Player> bases;
    
    public static BaseSituation empty() {
        return new BaseSituation(ImmutableMap.of());
    }
    
    public BaseSituation(@Nullable Player onFirst, @Nullable Player onSecond, @Nullable Player onThird) {
        ImmutableSortedMap.Builder<Base, Player> builder = ImmutableSortedMap.naturalOrder();
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
    
    public BaseSituation(Map<Base, Player> bases) {
        checkArgument(!bases.containsKey(Base.HOME));
        this.bases = ImmutableSortedMap.copyOf(bases);
    }

    public boolean isEmpty() {
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
    
    public EnumSet<Base> getOccupiedBases() {
        return isEmpty()
                ? EnumSet.noneOf(Base.class)
                : EnumSet.copyOf(this.bases.keySet());
    }
    
    public boolean isEmpty(Base base) {
        return !isOccupied(base);
    }
    
    public Player getRunner(Base base) {
        checkArgument(isOccupied(base));
        return this.bases.get(base);
    }
    
    public BaseSituation apply(Player batter, Map<Base, Base> advances) {
        Map<Base, Player> newSituation = new HashMap<>();
        for (Map.Entry<Base, Base> a : advances.entrySet()) {
            Base from = a.getKey();
            Player p = getPlayerOnBase(batter, from);
            Base to = a.getValue();
            if (to != Base.HOME) {
                newSituation.put(to, p);
            }
        }
        return new BaseSituation(newSituation);
    }
    
    // TODO: Does this method belong here? Or does this belong in some
    // other class, which is also responsible for the logic of what 
    // runners to advance and how far?
    // Answer: Eventually, yes. Once we start factoring in things like errors,
    // and runners taking extra bases or being thrown out. For now, when we are
    // still keeping things dirt simple, this is a good place for it.
    public ResultOfAdvance advanceRunners(Player batter, Outcome event) {
        switch (event) {
        case WALK: /* fall-through */
        case HIT_BY_PITCH:
            return advanceOnWalkOrHitByPitch(batter);
        case SINGLE:
            return advanceOnSingleOrDouble(batter, 1);
        case DOUBLE:
            return advanceOnSingleOrDouble(batter, 2);
        case TRIPLE:
            return advanceOnTriple(batter);
        case HOMERUN:
            return advanceOnHomeRun(batter);
        default:
            return new ResultOfAdvance(this, Collections.emptyList());
        }
    }

    private ResultOfAdvance advanceOnWalkOrHitByPitch(Player batter) {
        List<Base> forcedToAdvance = new ArrayList<>();
        if (isOccupied(Base.FIRST)) {
            forcedToAdvance.add(Base.FIRST);
            if (isOccupied(Base.SECOND)) {
                forcedToAdvance.add(Base.SECOND);
                if (isOccupied(Base.THIRD)) {
                    forcedToAdvance.add(Base.THIRD);
                }
            }
        }
        Map<Base, Player> newSituation = new HashMap<>(this.bases);
        List<Player> runs = new ArrayList<>();
        Collections.reverse(forcedToAdvance);
        for (Base base : forcedToAdvance) {
            Player p = newSituation.remove(base);
            if (base == Base.THIRD) {
                runs.add(p);
            } else {
                newSituation.put(base.next(), p);
            }
        }
        newSituation.put(Base.FIRST, batter);
        return new ResultOfAdvance(newSituation, runs);
    }
    
    private ResultOfAdvance advanceOnSingleOrDouble(Player batter, int bases) {
        assert bases == 1 || bases == 2;
        List<Player> runs = new ArrayList<>();
        Map<Base, Player> newSituation = new HashMap<>();
        for (Map.Entry<Base, Player> e : this.bases.entrySet()) {
            Base from = e.getKey();
            Player runner = e.getValue();
            if (Base.HOME.ordinal() - from.ordinal() > bases) {
                Base to = Base.values()[from.ordinal() + bases];
                newSituation.put(to, runner);
            } else {
                runs.add(runner);
            }
        }
        newSituation.put(Base.values()[bases - 1], batter);
        return new ResultOfAdvance(newSituation, runs);
    }
    
    private ResultOfAdvance advanceOnTriple(Player batter) {
        List<Player> runs = new ArrayList<>(this.bases.values());
        Collections.reverse(runs);
        return new ResultOfAdvance(new BaseSituation(null, null, batter), runs);
    }
    
    private ResultOfAdvance advanceOnHomeRun(Player batter) {
        List<Player> runs = new ArrayList<>(this.bases.values());
        Collections.reverse(runs);
        runs.add(batter);
        return new ResultOfAdvance(BaseSituation.empty(), runs);
    }
    
    private Player getPlayerOnBase(Player batter, Base base) {
        return (base == Base.HOME)
                ? batter
                : getRunner(base);
    }
    
    public List<Player> getScoringPlayers(Player batter, Map<Base, Base> advances) {
        return advances.entrySet().stream()
                .filter(e -> e.getValue().isHome())
                .map(e -> getPlayerOnBase(batter, e.getKey()))
                .collect(Collectors.toList());
    }
    
    public Map<Base, Player> toMap() {
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
        private final ImmutableList<Player> runs;
        
        public ResultOfAdvance(Map<Base, Player> newSituation, List<Player> runs) {
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
        public ResultOfAdvance(BaseSituation newSituation, List<Player> runs) {
            this.newSituation = requireNonNull(newSituation);
            this.runs = ImmutableList.copyOf(runs);
        }
        
        public BaseSituation getNewSituation() {
            return newSituation;
        }
        
        /**
         * Returns the runners that scored on the play, in the order that they scored.
         */
        public ImmutableList<Player> getRunnersThatScored() {
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
