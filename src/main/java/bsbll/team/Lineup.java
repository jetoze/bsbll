package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import bsbll.player.Player;

public final class Lineup {
    private final BattingOrder battingOrder;
    private final Player pitcher;
    
    // TODO: What about fielding positions?
    //       Should this class really be called BattingOrder for now, until we
    //       implement fielding positions?
    
    public Lineup(BattingOrder battingOrder, Player pitcher) {
        this.battingOrder = requireNonNull(battingOrder);
        this.pitcher = requireNonNull(pitcher);
        checkArgument(battingOrder.contains(pitcher));
    }
    
    public Player getPitcher() {
        return pitcher;
    }
    
    public BattingOrder getBattingOrder() {
        return battingOrder;
    }

}
