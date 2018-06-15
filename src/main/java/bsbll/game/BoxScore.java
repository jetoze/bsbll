package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import bsbll.game.event.GameEvents;
import bsbll.game.play.Play;
import bsbll.player.Player;
import bsbll.stats.PitchingStatLine;
import bsbll.team.Lineup;
import bsbll.team.Team;

public final class BoxScore {
    private final LineScore lineScore;
    private final Lineup homeLineup;
    private final Lineup visitingLineup;
    @Nullable
    private final PitcherOfRecord winningPitcher;
    @Nullable
    private final PitcherOfRecord losingPitcher;
    private final RunsScored runsScored;
    private final PlayerGameStats playerStats;
    private final ImmutableList<Play> plays;
    private final GameEvents gameEvents;

    /**
     * 
     * @param lineScore
     *            the game's linescore
     * @param homeLineup
     *            the home team's lineup
     * @param visitingLineup
     *            the road team's lineup
     * @param winningPitcher
     *            the winning pitcher, or {@code null} if the game was tied
     * @param losingPitcher
     *            the losing pitcher, or {@code null} if the game was tied
     * @param playerStats
     *            the individual player stats from the game
     * @param gameEvents
     *            the events that occurred during the game that should be
     *            reported in the box score
     */
    public BoxScore(LineScore lineScore, 
                    Lineup homeLineup, 
                    Lineup visitingLineup,
                    @Nullable PitcherOfRecord winningPitcher,
                    @Nullable PitcherOfRecord losingPitcher,
                    RunsScored runsScored,
                    PlayerGameStats playerStats, 
                    List<Play> plays,
                    GameEvents gameEvents) {
        this.lineScore = requireNonNull(lineScore);
        this.homeLineup = requireNonNull(homeLineup);
        this.visitingLineup = requireNonNull(visitingLineup);
        this.winningPitcher = winningPitcher;
        this.losingPitcher = losingPitcher;
        checkArgument((winningPitcher == null && losingPitcher == null) ||
                (winningPitcher != null && losingPitcher != null), 
                "Must provide both a winning and a losing pitcher, or neither. WP: %s. LP: %s",
                winningPitcher, losingPitcher);
        this.runsScored = requireNonNull(runsScored);
        this.playerStats = requireNonNull(playerStats);
        this.plays = ImmutableList.copyOf(plays);
        this.gameEvents = requireNonNull(gameEvents);
    }

    public Team getHomeTeam() {
        return lineScore.getHomeTeam();
    }
    
    public Team getVisitingTeam() {
        return lineScore.getVisitingTeam();
    }
    
    public Team getBattingTeam(Inning inning) {
        return inning.isTop()
                ? getVisitingTeam()
                : getHomeTeam();
    }

    public LineScore getLineScore() {
        return lineScore;
    }

    public Lineup getHomeLineup() {
        return homeLineup;
    }

    public Lineup getVisitingLineup() {
        return visitingLineup;
    }

    public PlayerGameStats getPlayerStats() {
        return playerStats;
    }
    
    public PitchingStatLine getPitchingLine(Player player) {
        return playerStats.getPitchingLine(player);
    }
    
    public ImmutableList<Play> getPlays() {
        return plays;
    }

    public GameEvents getGameEvents() {
        return this.gameEvents;
    }

    public RunsScored getRunsScored() {
        return runsScored;
    }

    public GameResult toGameResult() {
        return lineScore.toGameResult();
    }
    
    public Optional<PitcherOfRecord> checkPitcherOfRecord(Player pitcher) {
        requireNonNull(pitcher);
        if (winningPitcher != null && winningPitcher.getPitcher() == pitcher) {
            return Optional.ofNullable(winningPitcher);
        } else if (losingPitcher != null &&  losingPitcher.getPitcher() == pitcher) {
            return Optional.ofNullable(losingPitcher);
        } else {
            return Optional.empty();
        }
    }
}
