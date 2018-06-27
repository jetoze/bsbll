package bsbll.game.event;

import bsbll.game.Inning;
import bsbll.player.Player;

public final class BalkEvent extends PitchingEvent {
    public BalkEvent(Inning inning, Player pitcher, int seasonTotal) {
        super(inning, pitcher, seasonTotal);
    }
}
