package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

@NotThreadSafe
public final class BattingOrder {
    private final ImmutableList<Player> batters;
    private int nextBatter;
    
    // TODO: Should this class be mutable, via its nextBatter field? Or do we 
    // keep track of the next batter elsewhere, for example in a game-specific
    // structure that keeps track of the state of the current game?
    // Keeping nextBatter here for now, but the other alternative sounds
    // more attractive.

    public BattingOrder(List<Player> batters) {
        checkArgument(batters.size() == 9);
        this.batters = ImmutableList.copyOf(batters);
    }
    
    public Player nextBatter() {
        Player batter = batters.get(nextBatter);
        ++nextBatter;
        if (nextBatter == batters.size()) {
            nextBatter = 0;
        }
        return batter;
    }
    
    boolean contains(Player p) {
        return batters.contains(p);
    }

}
