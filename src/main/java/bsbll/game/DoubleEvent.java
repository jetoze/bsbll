package bsbll.game;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

@Immutable
public final class DoubleEvent extends ExtraBaseHitEvent {
    public DoubleEvent(Player batter, Player pitcher) {
        super(Outcome.DOUBLE, batter, pitcher);
    }
}
