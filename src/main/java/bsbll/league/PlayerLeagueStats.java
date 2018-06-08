package bsbll.league;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import bsbll.game.BoxScore;
import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingLeaders;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStatLine;
import bsbll.stats.PlayerStatLookup;

@NotThreadSafe
public final class PlayerLeagueStats {
    private final Map<PlayerId, BattingStatLine> battingStats = new HashMap<>();
    private final Map<PlayerId, PitchingStatLine> pitchingStats = new HashMap<>();

    public void update(BoxScore boxScore) {
        update(boxScore.getPlayerStats());
    }
    
    public void update(PlayerGameStats gameStats) {
        gameStats.gatherBattingStats((p, line) -> battingStats.merge(p, line, BattingStatLine::plus));
        gameStats.gatherPitchingStats((p, line) -> pitchingStats.merge(p, line, PitchingStatLine::plus));
    }

    public BattingStatLine getBattingStats(Player player) {
        return getBattingStats(player.getId());
    }
    
    public BattingStatLine getBattingStats(PlayerId playerId) { 
        requireNonNull(playerId);
        BattingStatLine line = battingStats.get(playerId);
        if (line == null) {
            // This can happen e.g. when looking up the current stats for a player
            // that's playing his very first game.
            line = BattingStatLine.empty();
        }
        return line;
    }
    
    public <T> BattingLeaders<T> getBattingLeaders(BattingStat<T> stat, int top) {
        return BattingLeaders.forStat(this.battingStats, stat, top);
    }
    
    public <T> BattingLeaders<T> getBattingLeaders(BattingStat<T> stat, int top, int minAtBats) {
        return BattingLeaders.forStat(this.battingStats, stat, top, minAtBats);
    }

    public PitchingStatLine getPitchingStats(Player player) {
        return getPitchingStats(player.getId());
    }
    
    public PitchingStatLine getPitchingStats(PlayerId playerId) {
        requireNonNull(playerId);
        PitchingStatLine line = pitchingStats.get(playerId);
        checkArgument(line != null, "No such player: %s", playerId);
        return line;
    }
    
    public PlayerStatLookup asLookup() {
        return new PlayerStatLookup() {
            @Override
            public <T> T getPitchingStat(Player player, PitchingStat<T> stat) {
                requireNonNull(player);
                requireNonNull(stat);
                PitchingStatLine line = getPitchingStats(player);
                return line.get(stat);
            }
            
            @Override
            public <T> T getBattingStat(Player player, BattingStat<T> stat) {
                requireNonNull(player);
                requireNonNull(stat);
                BattingStatLine line = getBattingStats(player);
                return line.get(stat);
            }
        };
    }
}
