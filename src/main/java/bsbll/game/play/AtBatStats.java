package bsbll.game.play;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

@Immutable
final class AtBatStats {
    /**
     * Offensive stats for the batter and base runners.
     */
    private final ImmutableTable<PlayerId, PrimitiveBattingStat, Integer> battingStats;
    /**
     * The stats of the pitcher.
     */
    private final ImmutableMap<PrimitivePitchingStat, Integer> pitcherStats;
    
    public AtBatStats(Table<PlayerId, PrimitiveBattingStat, Integer> battingStats,
                      Map<PrimitivePitchingStat, Integer> pitcherStats) {
        this.battingStats = ImmutableTable.copyOf(battingStats);
        this.pitcherStats = ImmutableMap.copyOf(pitcherStats);
    }
    
    public void applyTo(PlayerGameStats gameStats, Player pitcher) {
        for (PlayerId id : battingStats.rowKeySet()) {
            ImmutableMap<PrimitiveBattingStat, Integer> stats = battingStats.row(id);
            gameStats.updateBattingStats(id, stats);
        }
        gameStats.updatePitchingStats(pitcher.getId(), pitcherStats);
    }
    
    public static Builder builder() {
        return new Builder();
    }

    
    public static final class Builder {
        private final Table<PlayerId, PrimitiveBattingStat, Integer> battingStats = HashBasedTable.create();
        private final Map<PrimitivePitchingStat, Integer> pitcherStats = new HashMap<>();
        
        public Builder add(Player p, PrimitiveBattingStat stat, int value) {
            requireNonNull(p);
            requireNonNull(stat);
            checkPositive(value);
            Integer currentValue = battingStats.get(p.getId(), stat);
            int newValue = (currentValue != null)
                    ? currentValue + value
                    : value;
            battingStats.put(p.getId(), stat, newValue);
            return this;
        }
        
        public Builder add(PrimitivePitchingStat stat, int value) {
            requireNonNull(stat);
            checkPositive(value);
            pitcherStats.merge(stat, value, (v1, v2) -> v1 + v2);
            return this;
        }
        
        public AtBatStats build() {
            return new AtBatStats(battingStats, pitcherStats);
        }
    }
}
