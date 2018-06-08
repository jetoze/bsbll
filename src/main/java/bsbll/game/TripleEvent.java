package bsbll.game;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

@Immutable
public final class TripleEvent extends ExtraBaseHitEvent {
    public TripleEvent(Inning inning, Player batter, Player pitcher, int seasonTotal) {
        super(Outcome.TRIPLE, inning, batter, pitcher, seasonTotal);
    }
}
