package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

public final class TeamName {
    private final String mainName;
    private final String nickName;
    private final String abbreviation;
    
    /**
     * 
     * @param mainName for example "Brooklyn", "Minnesota"
     * @param nickName for example "Dodgers", "Twins"
     * @param abbreviation for example "BKN", "MIN"
     */
    public TeamName(String mainName, String nickName, String abbreviation) {
        this.mainName = checkNotEmpty(mainName);
        this.nickName = checkNotEmpty(nickName);
        checkArgument(abbreviation.length() == 3);
        this.abbreviation = abbreviation;
    }

    public String getMainName() {
        return mainName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getFullName() {
        return mainName + " " + nickName;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
}
