package bsbll.game.event;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import bsbll.bases.BaseSituation;
import bsbll.game.Inning;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.PitchingStat;
import bsbll.stats.PlayerStatLookup;
import bsbll.stats.Stat;

// TODO: Terrible name, of course.
// TODO: Unit tests.
public final class DefaultGameEventDetector implements GameEventDetector {
    private final PlayerStatLookup totalsLookup;
    private final Table<PlayerId, Stat<Integer>, Integer> totals = HashBasedTable.create();
    
    public DefaultGameEventDetector(PlayerStatLookup totalsLookup) {
        this.totalsLookup = requireNonNull(totalsLookup);
    }

    @Override
    public Optional<GameEvent> examine(PlayOutcome outcome, 
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
    private GameEvent examineImpl(PlayOutcome outcome, 
                                  Inning inning,
                                  Player batter, 
                                  Player pitcher, 
                                  int outs, 
                                  BaseSituation baseSituation) {
        switch (outcome.getType()) {
        case DOUBLE:
            int seasonTotal2B = updateSeasonTotal(batter, BattingStat.DOUBLES, totalsLookup::getBattingStat);
            return new DoubleEvent(inning, batter, pitcher, seasonTotal2B);
        case TRIPLE:
            int seasonTotal3B = updateSeasonTotal(batter, BattingStat.TRIPLES, totalsLookup::getBattingStat);
            return new TripleEvent(inning, batter, pitcher, seasonTotal3B);
        case HOMERUN:
            int seasonTotalHR = updateSeasonTotal(batter, BattingStat.HOMERUNS, totalsLookup::getBattingStat);
            return HomerunEvent.builder(inning, batter, pitcher)
                    .withOuts(outs)
                    .withSeasonTotal(seasonTotalHR)
                    .withRunnersOn(baseSituation.getNumberOfRunners())
                    .build();
        case HIT_BY_PITCH:
            int seasonTotalHBP = updateSeasonTotal(batter, BattingStat.HIT_BY_PITCHES, totalsLookup::getBattingStat);
            int pitcherSeasonTotalHBP = updateSeasonTotal(pitcher, PitchingStat.HIT_BY_PITCHES, totalsLookup::getPitchingStat);
            return new HitByPitchEvent(inning, batter, pitcher, seasonTotalHBP, pitcherSeasonTotalHBP);
        case WILD_PITCH:
            int seasonTotalWP = updateSeasonTotal(pitcher, PitchingStat.WILD_PITCHES, totalsLookup::getPitchingStat);
            return new WildPitchEvent(inning, pitcher, seasonTotalWP);
        case PASSED_BALL:
            // TODO: Implement me
            return null;
        case BALK:
            int seasonTotalBK = updateSeasonTotal(pitcher, PitchingStat.BALKS, totalsLookup::getPitchingStat);
            return new BalkEvent(inning, pitcher, seasonTotalBK);
        default:
            // Not of interest.
            return null;
        }
    }
    
    private <T extends Stat<Integer>> int updateSeasonTotal(Player player, T stat, 
                                        BiFunction<Player, T, Integer> lookupFunction) {
        Integer current = totals.get(player.getId(), stat);
        if (current == null) {
            current = lookupFunction.apply(player, stat);
        }
        ++current;
        totals.put(player.getId(), stat, current);
        return current;
    }

}
