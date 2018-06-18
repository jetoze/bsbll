package bsbll.game.play;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

@Immutable
final class AtBatStats {
    /**
     * The stats of the batter, exluding runs.
     */
    private final ImmutableMap<PrimitiveBattingStat, Integer> batterStats;
    /**
     * The stats of the pitcher.
     */
    private final ImmutableMap<PrimitivePitchingStat, Integer> pitcherStats;
    /**
     * The IDs of the players that scored a run during the at bat.
     */
    private final ImmutableSet<PlayerId> runsScored;
    /**
     * The IDs of the runners that stole bases during the at bat. (A multiset, since the 
     * same runner can steal more than one base during the at at.)
     */
    private final ImmutableMultiset<PlayerId> stolenBases;
    /**
     * The IDs of the runners that were caught stealing during the at bat.
     */
    private final ImmutableSet<PlayerId> caughtStealing;
    
    public AtBatStats(Map<PrimitiveBattingStat, Integer> batterStats,
                      Map<PrimitivePitchingStat, Integer> pitcherStats,
                      Set<PlayerId> runsScored,
                      Multiset<PlayerId> stolenBases,
                      Set<PlayerId> caughtStealing) {
        this.batterStats = ImmutableMap.copyOf(batterStats);
        this.pitcherStats = ImmutableMap.copyOf(pitcherStats);
        this.runsScored = ImmutableSet.copyOf(runsScored);
        this.stolenBases = ImmutableMultiset.copyOf(stolenBases);
        this.caughtStealing = ImmutableSet.copyOf(caughtStealing);
    }
    
    public void applyTo(Player batter, Player pitcher, PlayerGameStats gameStats) {
        gameStats.updateBattingStats(batter.getId(), batterStats);
        gameStats.updatePitchingStats(pitcher.getId(), pitcherStats);
        runsScored.forEach(id -> gameStats.add(id, BattingStat.RUNS, 1));
        stolenBases.forEachEntry((id, value) -> gameStats.add(id, BattingStat.STOLEN_BASES, value));
        caughtStealing.forEach(id -> gameStats.add(id, BattingStat.CAUGHT_STEALING, 1));
    }
    
    public static Builder builder() {
        return new Builder();
    }

    
    public static final class Builder {
        private final Map<PrimitiveBattingStat, Integer> batterStats = new HashMap<>();
        private final Map<PrimitivePitchingStat, Integer> pitcherStats = new HashMap<>();
        private final Set<PlayerId> runsScored = new HashSet<>();
        private final Multiset<PlayerId> stolenBases = HashMultiset.create();
        private final Set<PlayerId> caughtStealing = new HashSet<>();
        
        public Builder add(PrimitiveBattingStat stat, int value) {
            requireNonNull(stat);
            checkPositive(value);
            batterStats.merge(stat, value, (v1, v2) -> v1 + v2);
            return this;
        }
        
        public Builder add(PrimitivePitchingStat stat, int value) {
            requireNonNull(stat);
            checkPositive(value);
            pitcherStats.merge(stat, value, (v1, v2) -> v1 + v2);
            return this;
        }
        
        public Builder scored(Player player) {
            this.runsScored.add(player.getId());
            return this;
        }
        
        public Builder stoleBase(Player player) {
            this.stolenBases.add(player.getId());
            return this;
        }
        
        public Builder caughtStealing(Player player) {
            this.caughtStealing.add(player.getId());
            return this;
        }
        
        public AtBatStats build() {
            return new AtBatStats(batterStats, pitcherStats, runsScored, stolenBases, caughtStealing);
        }
    }
}
