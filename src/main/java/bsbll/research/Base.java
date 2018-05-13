package bsbll.research;

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

    public int intValueWhenOrigin() {
        return isHome()
                ? 0
                : ordinal() + 1;
    }
    
    public Base preceding() {
        return (this == FIRST)
                ? HOME
                : values()[ordinal() - 1];
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
}
