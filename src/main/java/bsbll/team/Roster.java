package bsbll.team;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

public final class Roster {
    private final ImmutableList<Player> batters;
    private final Rotation rotation;

    public Roster(List<Player> batters, List<Player> startingPitchers) {
        this.batters = ImmutableList.copyOf(batters);
        this.rotation = new Rotation(startingPitchers);
    }

    /**
     * Gets the lineup for a new game.
     * <p>
     * A new {@code Lineup} instance is returned by each call.
     */
    public Lineup getLineup() {
        Player pitcher = rotation.nextStartingPitcher();
        List<Player> battingOrder = new ArrayList<>(batters);
        battingOrder.add(pitcher);
        return new Lineup(battingOrder, pitcher);
    }
    
}
