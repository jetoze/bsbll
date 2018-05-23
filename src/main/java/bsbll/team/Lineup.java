package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

public final class Lineup {
    private final ImmutableList<Player> batters;
    private final Player pitcher;
    private int nextBatter;
    
    // TODO: What about fielding positions?
    //       Should this class really be called BattingOrder for now, until we
    //       implement fielding positions?
    
    // TODO: Should this class be mutable, via its nextBatter field? Or do we 
    // keep track of the next batter elsewhere, for example in a game-specific
    // structure that keeps track of the state of the current game?
    // Keeping nextBatter here for now, but the other alternative sounds
    // more attractive.
    
    public Lineup(List<Player> batters, Player pitcher) {
        checkArgument(batters.size() == 9);
        checkArgument(batters.contains(pitcher));
        this.pitcher = requireNonNull(pitcher);
        this.batters = ImmutableList.copyOf(batters);
    }
    
    public Player getPitcher() {
        return pitcher;
    }
    
    public Player nextBatter() {
        Player batter = batters.get(nextBatter);
        ++nextBatter;
        if (nextBatter == batters.size()) {
            nextBatter = 0;
        }
        return batter;
    }

}
