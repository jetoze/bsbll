package bsbll.game;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

@Immutable
public final class TripleEvent extends ExtraBaseHitEvent {
    public TripleEvent(Player batter, Player pitcher) {
        super(Outcome.TRIPLE, batter, pitcher);
    }
}
