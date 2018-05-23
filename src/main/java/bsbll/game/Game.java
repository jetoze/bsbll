package bsbll.game;

import static java.util.Objects.requireNonNull;

import bsbll.team.Team;

public final class Game {
    private final Team homeTeam;
    private final Team roadTeam;

    public Game(Team homeTeam, Team roadTeam) {
        this.homeTeam = requireNonNull(homeTeam);
        this.roadTeam = requireNonNull(roadTeam);
    }
    

}
