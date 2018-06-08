package bsbll.game.event;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import bsbll.game.BaseSituation;
import bsbll.game.Inning;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.PlayerStatLookup;

// TODO: Terrible name, of course.
// TODO: Unit tests.
public final class DefaultGameEventDetector implements GameEventDetector {
    private final PlayerStatLookup totalsLookup;
    private final Table<PlayerId, BattingStat<Integer>, Integer> battingTotals = HashBasedTable.create();
    
    public DefaultGameEventDetector(PlayerStatLookup totalsLookup) {
        this.totalsLookup = requireNonNull(totalsLookup);
    }

    @Override
    public Optional<GameEvent> examine(Outcome outcome, 
                                       Inning inning,
                                       Player batter, 
                                       Player pitcher, 
                                       int outs, 
                                       BaseSituation baseSituation) {
        requireNonNull(outcome);
        requireNonNull(inning);
        requireNonNull(batter);
        requireNonNull(pitcher);
        checkInRange(outs, 0, 2);
        requireNonNull(baseSituation);
        GameEvent event = examineImpl(outcome, inning, batter, pitcher, outs, baseSituation);
        return Optional.ofNullable(event);
    }
    
    @Nullable
    private GameEvent examineImpl(Outcome outcome, 
                                  Inning inning,
                                  Player batter, 
                                  Player pitcher, 
                                  int outs, 
                                  BaseSituation baseSituation) {
        switch (outcome) {
        case DOUBLE:
            int seasonTotal2B = updateSeasonTotal(batter, BattingStat.DOUBLES);
            return new DoubleEvent(inning, batter, pitcher, seasonTotal2B);
        case TRIPLE:
            int seasonTotal3B = updateSeasonTotal(batter, BattingStat.TRIPLES);
            return new TripleEvent(inning, batter, pitcher, seasonTotal3B);
        case HOMERUN:
            int seasonTotalHR = updateSeasonTotal(batter, BattingStat.HOMERUNS);
            return HomerunEvent.builder(inning, batter, pitcher)
                    .withOuts(outs)
                    .withSeasonTotal(seasonTotalHR)
                    .withRunnersOn(baseSituation.getNumberOfRunners())
                    .build();
        case HIT_BY_PITCH:
            int seasonTotalHBP = updateSeasonTotal(batter, BattingStat.HIT_BY_PITCHES);
            return new HitByPitchEvent(inning, batter, pitcher, seasonTotalHBP);
        default:
            // Not of interest.
            return null;
        }
    }
    
    private int updateSeasonTotal(Player batter, BattingStat<Integer> stat) {
        Integer current = battingTotals.get(batter.getId(), stat);
        if (current == null) {
            current = totalsLookup.getBattingStat(batter, stat);
        }
        ++current;
        battingTotals.put(batter.getId(), stat, current);
        return current;
    }

}
