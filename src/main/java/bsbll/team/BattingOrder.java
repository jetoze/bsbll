package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

@NotThreadSafe
public final class BattingOrder {
    private final ImmutableList<Player> batters;
    private Player currentBatter;
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
    
    public static BattingOrder of(List<Player> batters) {
        return new BattingOrder(batters);
    }
    
    public ImmutableList<Player> getBatters() {
        return batters;
    }
    
    public Player nextBatter() {
        Player batter = batters.get(nextBatter);
        ++nextBatter;
        if (nextBatter == batters.size()) {
            nextBatter = 0;
        }
        this.currentBatter = batter;
        return batter;
    }
    
    public void returnBatter(Player batter) {
        requireNonNull(batter);
        checkArgument(batter == this.currentBatter, "%s is not the current batter.", batter);
        --nextBatter;
        if (nextBatter == -1) {
            nextBatter = 8;
        }
    }
    
    boolean contains(Player p) {
        return batters.contains(p);
    }
}
