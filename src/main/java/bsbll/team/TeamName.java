package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

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
}
