package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;
import bsbll.stats.PitchingStatLine;

public final class PlayerGameStats {
    private final Map<PlayerId, BattingStatLine.Builder> battingStats = new HashMap<>();
    private final Map<PlayerId, PitchingStatLine.Builder> pitchingStats = new HashMap<>();
    
    public void updateBattingStats(PlayerId playerId, Map<PrimitiveBattingStat, Integer> stats) {
        BattingStatLine.Builder line = battingStats(playerId);
        stats.entrySet().forEach(e -> line.add(e.getKey(), e.getValue()));
    }
    
    public void updatePitchingStats(PlayerId playerId, Map<PrimitivePitchingStat, Integer> stats) {
        PitchingStatLine.Builder line = pitchingStats(playerId);
        stats.entrySet().forEach(e -> line.add(e.getKey(), e.getValue()));
    }
    
    public void add(Player player, PrimitiveBattingStat stat, int value) {
        add(player.getId(), stat, value);
    }
    
    public void add(PlayerId playerId, PrimitiveBattingStat stat, int value) {
        battingStats(playerId).add(stat, value);
    }
    
    public void add(Player player, PrimitivePitchingStat stat, int value) {
        add(player.getId(), stat, value);
    }
    
    public void add(PlayerId playerId, PrimitivePitchingStat stat, int value) {
        pitchingStats(playerId).add(stat, value);
    }
    
    public void update(Player batter, Player pitcher, PlayOutcome outcome, List<BaseRunner> runs) {
        updateBatterStats(batter, outcome, runs.size());
        updateStatsForRunnersThatScored(batter, runs);
        updatePitchingStats(pitcher, outcome, runs.size());
    }
    
    private void updateBatterStats(Player batter, PlayOutcome outcome, int rbis) {
        BattingStatLine.Builder line = battingStats(batter);
        line.add(outcome, rbis);
    }
    
    private void updateStatsForRunnersThatScored(Player batter, List<BaseRunner> runs) {
        runs.stream()
            .map(BaseRunner::getRunner)
            .filter(p -> p != batter).forEach(p -> {
                BattingStatLine.Builder line = battingStats(p);
                line.add(BattingStat.RUNS, 1);
            });
    }
    
    private BattingStatLine.Builder battingStats(Player player) {
        return battingStats(player.getId());
    }

    private BattingStatLine.Builder battingStats(PlayerId id) {
        return battingStats.computeIfAbsent(id, i -> BattingStatLine.forNewGame());
    }
    
    private void updatePitchingStats(Player pitcher, PlayOutcome outcome, int numberOfRuns) {
        PitchingStatLine.Builder line = pitchingStats(pitcher);
        line.add(outcome, numberOfRuns);
    }
    
    private PitchingStatLine.Builder pitchingStats(Player player) {
        return pitchingStats(player.getId());
    }

    private PitchingStatLine.Builder pitchingStats(PlayerId id) {
        return pitchingStats.computeIfAbsent(id, i -> PitchingStatLine.forNewGame());
    }
    
    public BattingStatLine getBattingLine(Player player) {
        BattingStatLine.Builder builder = battingStats.get(player.getId());
        checkArgument(builder != null, "No such player: %s", player);
        return builder.build();
    }

    public PitchingStatLine getPitchingLine(Player player) {
        PitchingStatLine.Builder builder = pitchingStats.get(player.getId());
        checkArgument(builder != null, "No such player: %s", player);
        return builder.build();
    }

    public void updatePitchersOfRecord(PitcherOfRecord wp, PitcherOfRecord lp) {
        pitchingStats(wp.getPitcher()).set(PitchingStat.WINS, 1);
        pitchingStats(lp.getPitcher()).set(PitchingStat.LOSSES, 1);
        // TODO: This will change once we implement pitcher substitutions
        pitchingStats(wp.getPitcher()).set(PitchingStat.COMPLETE_GAMES, 1);
        if (pitchingStats(wp.getPitcher()).get(PitchingStat.RUNS) == 0) {
            pitchingStats(wp.getPitcher()).set(PitchingStat.SHUTOUTS, 1);
        }
        pitchingStats(lp.getPitcher()).set(PitchingStat.COMPLETE_GAMES, 1);
    }
    
    public void gatherBattingStats(BiConsumer<PlayerId, BattingStatLine> c) {
        battingStats.forEach((id, builder) -> c.accept(id, builder.build()));
    }
    
    public void gatherPitchingStats(BiConsumer<PlayerId, PitchingStatLine> c) {
        pitchingStats.forEach((id, builder) -> c.accept(id, builder.build()));
    }
}