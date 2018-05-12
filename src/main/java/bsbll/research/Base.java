package bsbll.research;

public enum Base {
    FIRST,
    SECOND,
    THIRD,
    HOME;
    
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
    
    public boolean isHome() {
        return this == HOME;
    }
}
