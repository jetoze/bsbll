package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

@NotThreadSafe
public final class Rotation {
    private final ImmutableList<Player> pitchers;
    private int nextStartingPitcher;
    
    // TODO: Same question as for Lineup. Where should we keep track of the 
    // next starting pitcher?
    
    public Rotation(List<Player> pitchers) {
        checkArgument(!pitchers.isEmpty());
        this.pitchers = ImmutableList.copyOf(pitchers);
    }
    
    public Player nextStartingPitcher() {
        Player p = pitchers.get(nextStartingPitcher);
        ++nextStartingPitcher;
        if (nextStartingPitcher == pitchers.size()) {
            nextStartingPitcher = 0;
        }
        return p;
    }

}
