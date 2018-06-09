package bsbll.game.event;

import static tzeth.preconds.MorePreconditions.checkPositive;

import javax.annotation.concurrent.Immutable;

import bsbll.game.BattingEvent;
import bsbll.game.Inning;
import bsbll.player.Player;

@Immutable
public final class HitByPitchEvent extends BattingEvent {
    private final int pitcherSeasonTotal;
    
    public HitByPitchEvent(Inning inning, Player batter, Player pitcher, int seasonTotal, int pitcherSeasonTotal) {
        super(inning, batter, pitcher, seasonTotal);
        this.pitcherSeasonTotal = checkPositive(pitcherSeasonTotal);
    }

    public int getPitcherSeasonTotal() {
        return pitcherSeasonTotal;
    }
}
