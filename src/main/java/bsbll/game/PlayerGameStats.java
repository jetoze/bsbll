package bsbll.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;
import bsbll.stats.PitchingStatLine;

public final class PlayerGameStats {
    private final Map<PlayerId, BattingStatLine.Builder> battingStats = new HashMap<>();
    private final Map<PlayerId, PitchingStatLine.Builder> pitchingStats = new HashMap<>();
    
    public void update(Player batter, Player pitcher, Outcome outcome, List<Player> runs) {
        updateBatterStats(batter, outcome, runs.size());
        updateStatsForRunnersThatScored(batter, runs);
        updatePitchingStats(pitcher, outcome, runs.size());
    }
    
    private void updateBatterStats(Player batter, Outcome outcome, int rbis) {
        BattingStatLine.Builder line = battingStats(batter);
        line.add(outcome, rbis);
    }
    
    private void updateStatsForRunnersThatScored(Player batter, List<Player> runs) {
        runs.stream().filter(p -> p != batter).forEach(p -> {
            BattingStatLine.Builder line = battingStats(batter);
            line.add(BattingStat.RUNS, 1);
        });
    }
    
    private BattingStatLine.Builder battingStats(Player player) {
        BattingStatLine.Builder builder = battingStats.get(player.getId());
        if (builder == null) {
            builder = BattingStatLine.builder().set(PrimitiveBattingStat.GAMES, 1);
            battingStats.put(player.getId(), builder);
        }
        return builder;
    }
    
    private void updatePitchingStats(Player pitcher, Outcome outcome, int numberOfRuns) {
        PitchingStatLine.Builder line = pitchingStats(pitcher);
        line.add(outcome, numberOfRuns);
    }
    
    private PitchingStatLine.Builder pitchingStats(Player player) {
        PitchingStatLine.Builder builder = pitchingStats.get(player.getId());
        if (builder == null) {
            builder = PitchingStatLine.builder().set(PrimitivePitchingStat.GAMES, 1);
            pitchingStats.put(player.getId(), builder);
        }
        return builder;
    }
    
    public void gatherBattingStats(BiConsumer<PlayerId, BattingStatLine> c) {
        battingStats.forEach((id, builder) -> c.accept(id, builder.build()));
    }
    
    public void gatherPitchingStats(BiConsumer<PlayerId, PitchingStatLine> c) {
        pitchingStats.forEach((id, builder) -> c.accept(id, builder.build()));
    }
}