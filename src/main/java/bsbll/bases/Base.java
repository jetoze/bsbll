package bsbll.bases;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.Comparator;

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
        return advance(baseHit.value());
    }
    
    public Advance advance(int numBases) {
        checkNotNegative(numBases);
        int max = Math.min(numBases, 4);
        int o = (this == HOME) 
                ? max - 1
                : Math.min(ordinal() + max, 3);
        Base to = values()[o];
        return Advance.safe(this, to);
    }
    
    public static Comparator<Base> comparingOrigin() {
        return Comparator.comparing(Base::intValueWhenOrigin).reversed();
    }
}
