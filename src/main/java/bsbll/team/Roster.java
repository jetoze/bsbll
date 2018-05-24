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
        BattingOrder bo = createBattingOrder(pitcher);
        return new Lineup(bo, pitcher);
    }

    private BattingOrder createBattingOrder(Player pitcher) {
        List<Player> bo = new ArrayList<>(batters);
        bo.add(pitcher);
        return new BattingOrder(bo);
    }
    
}
