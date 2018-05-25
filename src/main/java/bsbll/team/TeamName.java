package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.function.Function;

public final class TeamName {
    private final String mainName;
    private final String nickname;
    private final String abbreviation;
    
    /**
     * 
     * @param mainName for example "Brooklyn", "Minnesota"
     * @param nickname for example "Dodgers", "Twins"
     * @param abbreviation for example "BKN", "MIN"
     */
    public TeamName(String mainName, String nickname, String abbreviation) {
        this.mainName = checkNotEmpty(mainName);
        this.nickname = checkNotEmpty(nickname);
        checkArgument(abbreviation.length() == 3);
        this.abbreviation = abbreviation;
    }

    public String getMainName() {
        return mainName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFullName() {
        return mainName + " " + nickname;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    
    public enum Mode implements Function<TeamName, String> {
        NONE,
        MAIN,
        FULL,
        ABBREV;
        
        @Override
        public final String apply(TeamName name) {
            switch (this) { 
            case NONE:
                return "";
            case MAIN:
                return name.getMainName();
            case FULL:
                return name.getFullName();
            case ABBREV:
                return name.getAbbreviation();
            default:
                throw new AssertionError("Unknown TeamNameMode: " + this);
            }
        }
        
    }
}
