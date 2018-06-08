package bsbll.game.event;

import javax.annotation.concurrent.Immutable;

import bsbll.game.BattingEvent;
import bsbll.game.Inning;
import bsbll.player.Player;

@Immutable
public final class DoubleEvent extends BattingEvent {
    public DoubleEvent(Inning inning, Player batter, Player pitcher, int seasonTotal) {
        super(inning, batter, pitcher, seasonTotal);
    }
}
