package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
                                       Player batter, 
                                       Player pitcher, 
                                       int inning,
                                       int outs, 
                                       BaseSituation baseSituation) {
        requireNonNull(outcome);
        requireNonNull(batter);
        requireNonNull(pitcher);
        checkPositive(inning);
        checkInRange(outs, 0, 2);
        requireNonNull(baseSituation);
        GameEvent event = examineImpl(outcome, batter, pitcher, inning, outs, baseSituation);
        return Optional.ofNullable(event);
    }
    
    @Nullable
    private GameEvent examineImpl(Outcome outcome, 
                                       Player batter, 
                                       Player pitcher, 
                                       int inning,
                                       int outs, 
                                       BaseSituation baseSituation) {
        switch (outcome) {
        case DOUBLE:
            int seasonTotal2B = updateSeasonTotal(batter, BattingStat.DOUBLES);
            return new DoubleEvent(batter, pitcher, seasonTotal2B);
        case TRIPLE:
            int seasonTotal3B = updateSeasonTotal(batter, BattingStat.TRIPLES);
            return new TripleEvent(batter, pitcher, seasonTotal3B);
        case HOMERUN:
            int seasonTotalHR = updateSeasonTotal(batter, BattingStat.HOMERUNS);
            return HomerunEvent.builder(batter, pitcher)
                    .inInning(inning)
                    .withOuts(outs)
                    .withSeasonTotal(seasonTotalHR)
                    .withRunnersOn(baseSituation.getNumberOfRunners())
                    .build();
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
