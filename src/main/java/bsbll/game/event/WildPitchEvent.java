package bsbll.game.event;

import bsbll.game.Inning;
import bsbll.player.Player;

public final class WildPitchEvent extends PitchingEvent {
    public WildPitchEvent(Inning inning, Player pitcher, int seasonTotal) {
        super(inning, pitcher, seasonTotal);
    }
}
