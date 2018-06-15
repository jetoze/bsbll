package bsbll.bases;

import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum Base {
    FIRST,
    SECOND,
    THIRD,
    HOME;
    
    public boolean isHome() {
        return this == HOME;
    }
    
    public boolean isOccupiable() {
        return this != HOME;
    }

    private int intValueWhenOrigin() {
        return isHome()
                ? 0
                : ordinal() + 1;
    }
    
    public Base preceding() {
        return (this == FIRST)
                ? HOME
                : values()[ordinal() - 1];
    }
    
    public Base next() {
        return (this == HOME)
                ? FIRST
                : values()[ordinal() + 1];
    }
    
    public static Base fromChar(char c) {
        switch (c) {
        case '1':
            return FIRST;
        case '2':
            return SECOND;
        case '3':
            return THIRD;
        case 'H':
            return HOME;
        case 'B': // for batter
            return HOME;
        default:
            throw new IllegalArgumentException("Invalid base code: " + c);
        }
    }
    
    public static Base[] occupiable() {
        return new Base[] { FIRST, SECOND, THIRD };
    }
    
    public Advance defaultAdvance(BaseHit baseHit) {
        int o = (this == HOME) 
                ? baseHit.value() - 1
                : Math.min(ordinal() + baseHit.value(), 3);
        Base to = values()[o];
        return Advance.safe(this, to);
    }
    
    private static final ImmutableList<ImmutableSet<Base>> OCCUPIED_BASE_POSSIBILITIES = createOccupiedBasesPossibilities();
    
    private static ImmutableList<ImmutableSet<Base>> createOccupiedBasesPossibilities() {
        ImmutableList.Builder<ImmutableSet<Base>> list = ImmutableList.builder();
        list.add(Sets.immutableEnumSet(FIRST));
        list.add(Sets.immutableEnumSet(SECOND));
        list.add(Sets.immutableEnumSet(THIRD));
        list.add(Sets.immutableEnumSet(FIRST, SECOND));
        list.add(Sets.immutableEnumSet(FIRST, THIRD));
        list.add(Sets.immutableEnumSet(SECOND, THIRD));
        list.add(Sets.immutableEnumSet(FIRST, SECOND, THIRD));
        return list.build();
    }
    
    /**
     * Returns a list of all the different ways the bases can be occupied (runner on first, runners on first and second,
     * etc).
     */
    public static ImmutableList<ImmutableSet<Base>> occupiedBasesPossibilities() {
        return OCCUPIED_BASE_POSSIBILITIES;
    }
    
    public static Comparator<Base> comparingOrigin() {
        return Comparator.comparing(Base::intValueWhenOrigin).reversed();
    }
}
