package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import bsbll.game.RunsScored.Run;
import bsbll.game.event.GameEvent;
import bsbll.game.event.GameEventDetector;
import bsbll.game.event.GameEvents;
import bsbll.game.play.Play;
import bsbll.team.Lineup;
import bsbll.team.Team;
import tzeth.collections.LoopingIterator;

public final class Game {
    private final Team homeTeam;
    private final Lineup homeLineup;
    
    private final Team visitingTeam;
    private final Lineup visitingLineup;
    
    private final GamePlayDriver driver;
    
    private final OfficialScorer officialScorer;

    private final Innings innings = new Innings();
    private final PlayerGameStats playerStats = new PlayerGameStats();
    // The event detector is not integral to playing a game, so it is optional.
    private GameEventDetector eventDetector = GameEventDetector.NO_EVENTS;

    public Game(Team homeTeam, Team visitingTeam, GamePlayDriver driver, OfficialScorer officialScorer) {
        requireNonNull(homeTeam);
        requireNonNull(visitingTeam);
        checkArgument(homeTeam != visitingTeam, "A team cannot play a game against itself (%s)", homeTeam);
        this.driver = requireNonNull(driver);
        this.officialScorer = requireNonNull(officialScorer);
        this.homeTeam = homeTeam;
        this.homeLineup = homeTeam.getRoster().getLineup();
        this.visitingTeam = visitingTeam;
        this.visitingLineup = visitingTeam.getRoster().getLineup();
    }
    
    public void setGameEventDetector(GameEventDetector eventDetector) {
        this.eventDetector = requireNonNull(eventDetector);
    }
    
    public BoxScore run() {
        checkState(innings.isEmpty(), "Game already in progress");
        LoopingIterator<Lineup> battingLineup = LoopingIterator.of(visitingLineup, homeLineup);
        LoopingIterator<Lineup> fieldingLineup = LoopingIterator.of(homeLineup, visitingLineup);
        List<Play> plays = new ArrayList<>();
        List<Run> runs = new ArrayList<>();
        List<GameEvent> events = new ArrayList<>();
        do {
            Lineup batting = battingLineup.next();
            Lineup fielding = fieldingLineup.next();
            assert batting != fielding;
            HalfInning halfInning = new HalfInning(
                    innings.current(),
                    batting.getBattingOrder(),
                    fielding.getPitcher(),
                    driver,
                    playerStats,
                    eventDetector,
                    innings.runsNeededToWalkOf());
            HalfInning.Summary summary = halfInning.run();
            innings.onHalfInningCompleted(summary.getStats());
            plays.addAll(summary.getPlays());
            runs.addAll(summary.getRuns());
            events.addAll(summary.getEvents());
        } while (!innings.isGameOver());
        LineScore lineScore = new LineScore(
                new LineScore.Line(homeTeam, innings.bottom),
                new LineScore.Line(visitingTeam, innings.top)
        );
        RunsScored runsScored = RunsScored.of(runs);
        // TODO: This will bomb out when we allow ties
        PitcherOfRecord wp = officialScorer.getWinningPitcher(homeTeam.getId(), homeLineup, 
                visitingTeam.getId(), visitingLineup, runsScored);
        PitcherOfRecord lp = officialScorer.getLosingPitcher(runsScored);
        playerStats.updatePitchersOfRecord(wp, lp);
        return new BoxScore(lineScore, homeLineup, visitingLineup, wp, lp, runsScored, 
                playerStats, plays, GameEvents.of(events));
    }
    
    @Override
    public String toString() {
        return String.format("%s vs %s", homeTeam.getName().getFullName(), visitingTeam.getName().getFullName());
    }

    
    private static class Innings {
        private final List<HalfInning.Stats> top = new ArrayList<>();
        private final List<HalfInning.Stats> bottom = new ArrayList<>();
        private Inning current = Inning.startOfGame();
        
        public Inning current() {
            return current;
        }
        
        public void onHalfInningCompleted(HalfInning.Stats stats) {
            if (current.isTop()) {
                top.add(stats);
            } else {
                bottom.add(stats);
            }
            current = current.next();
        }
        
        public int getHomeScore() {
            return countRuns(bottom);
        }
        
        public int getRoadScore() {
            return countRuns(top);
        }
        
        private static int countRuns(List<HalfInning.Stats> list) {
            return list.stream()
                    .mapToInt(HalfInning.Stats::getRuns)
                    .sum();
        }
        
        public boolean isGameOver() {
            if (top.size() < 9) {
                return false;
            } else if (top.size() == 9 && isInMiddleOf()) {
                return getHomeScore() > getRoadScore();
            } else {
                // extra innings
                return !isInMiddleOf() && (getHomeScore() != getRoadScore());
            }
        }
        
        private boolean isInMiddleOf() {
            return top.size() > bottom.size();
        }
        
        public RunsNeededToWin runsNeededToWalkOf() {
            if (top.size() >= 9 && isInMiddleOf()) {
                int value = getRoadScore() - getHomeScore() + 1;
                checkState(value > 0);
                return RunsNeededToWin.of(value);
            } else {
                return RunsNeededToWin.notApplicable();
            }
        }
        
        public boolean isEmpty() {
            return top.isEmpty() && bottom.isEmpty();
        }
    }
}
