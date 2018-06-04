package bsbll.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStatLine;

public final class PlayerGameStats {
    private final Map<PlayerId, BattingStatLine> battingStats = new HashMap<>();
    private final Map<PlayerId, PitchingStatLine> pitchingStats = new HashMap<>();
    
    public void update(Player batter, Player pitcher, Outcome outcome, List<Player> runs) {
        updateBatterStats(batter, outcome, runs.size());
        updateStatsForRunnersThatScored(batter, runs);
        updatePitchingStats(pitcher, outcome, runs.size());
    }
    
    private void updateBatterStats(Player batter, Outcome outcome, int rbis) {
        BattingStatLine line = battingStats(batter);
        BattingStatLine newLine = line.plus(outcome, rbis);
        battingStats.put(batter.getId(), newLine);
    }
    
    private void updateStatsForRunnersThatScored(Player batter, List<Player> runs) {
        runs.stream().filter(p -> p != batter).forEach(p -> {
            BattingStatLine line = battingStats(batter);
            BattingStatLine newLine = line.plus(BattingStat.RUNS, 1);
            battingStats.put(p.getId(), newLine);
        });
    }
    
    private BattingStatLine battingStats(Player player) {
        return battingStats.getOrDefault(player.getId(), BattingStatLine.forNewGame());
    }
    
    private void updatePitchingStats(Player pitcher, Outcome outcome, int numberOfRuns) {
        PitchingStatLine line = pitchingStats.getOrDefault(pitcher.getId(), PitchingStatLine.forNewGame());
        PitchingStatLine newLine = line.plus(outcome, numberOfRuns);
        pitchingStats.put(pitcher.getId(), newLine);
    }
    
    public void gatherBattingStats(BiConsumer<PlayerId, BattingStatLine> c) {
        battingStats.forEach(c);
    }
    
    public void gatherPitchingStats(BiConsumer<PlayerId, PitchingStatLine> c) {
        pitchingStats.forEach(c);
    }
}