package bsbll;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import bsbll.player.Player;
import bsbll.team.TeamName;

public enum NameMode {
    NONE(n -> "", 0),
    MAIN(TeamName::getMainName, 14),
    FULL(TeamName::getFullName, 22),
    ABBREV(TeamName::getAbbreviation, 6);

    private final Function<TeamName, String> teamNameFunction;
    private final int teamNameWidth;
    
    private NameMode(Function<TeamName, String> teamNameFunction, int teamNameWidth) {
        this.teamNameFunction = teamNameFunction;
        this.teamNameWidth = teamNameWidth;
    }

    public String applyTo(TeamName teamName) {
        requireNonNull(teamName);
        return teamNameFunction.apply(teamName);
    }
    
    public int getWidthOfTeamName() {
        return teamNameWidth;
    }
    
    public String applyTo(Player player) {
        // TODO: Implement this properly once players have names
        requireNonNull(player);
        return (this == NONE)
                ? ""
                : player.getId().toString();
    }
}
