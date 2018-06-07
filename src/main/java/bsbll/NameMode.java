package bsbll;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import bsbll.team.TeamName;

public enum NameMode {
    NONE(n -> ""),
    MAIN(TeamName::getMainName),
    FULL(TeamName::getFullName),
    ABBREV(TeamName::getAbbreviation);

    private final Function<TeamName, String> teamNameFunction;
    
    private NameMode(Function<TeamName, String> teamNameFunction) {
        this.teamNameFunction = teamNameFunction;
    }

    public String applyTo(TeamName teamName) {
        requireNonNull(teamName);
        return teamNameFunction.apply(teamName);
    }
}
