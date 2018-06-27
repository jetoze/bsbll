package bsbll.game.event;

import javax.annotation.concurrent.Immutable;

import bsbll.game.Inning;
import bsbll.player.Player;

@Immutable
public final class TripleEvent extends BattingEvent {
    public TripleEvent(Inning inning, Player batter, Player pitcher, int seasonTotal) {
        super(inning, batter, pitcher, seasonTotal);
    }
}
