package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

public class BaseSituation {
    private final ImmutableMap<Base, Player> bases;
    
    public static BaseSituation empty() {
        return new BaseSituation(ImmutableMap.of());
    }
    
    public BaseSituation(@Nullable Player onFirst, @Nullable Player onSecond, @Nullable Player onThird) {
        ImmutableMap.Builder<Base, Player> builder = ImmutableMap.builder();
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
        this.bases = ImmutableMap.copyOf(bases);
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
}
