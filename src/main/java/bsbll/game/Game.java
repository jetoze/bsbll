package bsbll.game;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bsbll.matchup.MatchupRunner;
import bsbll.team.Lineup;
import bsbll.team.Team;
import tzeth.collections.LoopingIterator;

public final class Game {
    private final Team homeTeam;
    private final Lineup homeLineup;
    
    private final Team visitingTeam;
    private final Lineup visitingLineup;
    
    private final MatchupRunner matchupRunner;

    private final Innings innings = new Innings();

    public Game(Team homeTeam, Team visitingTeam, MatchupRunner matchupRunner) {
        this.homeTeam = homeTeam;
        this.homeLineup = homeTeam.getRoster().getLineup();
        this.visitingTeam = visitingTeam;
        this.visitingLineup = visitingTeam.getRoster().getLineup();
        this.matchupRunner = requireNonNull(matchupRunner);
    }
    
    public LineScore run() {
        checkState(innings.isEmpty(), "Game already in progress");
        LoopingIterator<Lineup> battingLineup = LoopingIterator.of(visitingLineup, homeLineup);
        LoopingIterator<Lineup> fieldingLineup = LoopingIterator.of(homeLineup, visitingLineup);
        do {
            Lineup batting = battingLineup.next();
            Lineup fielding = fieldingLineup.next();
            assert batting != fielding;
            HalfInning halfInning = new HalfInning(
                    batting.getBattingOrder(),
                    fielding.getPitcher(),
                    matchupRunner,
                    innings.runsNeededToWalkOf().orElse(0));
            HalfInning.Stats stats = halfInning.run();
            innings.add(stats);
        } while (!innings.isGameOver());
        return new LineScore(
                new LineScore.Line(homeTeam, innings.bottom),
                new LineScore.Line(visitingTeam, innings.top)
        );
    }

    
    private static class Innings {
        private final List<HalfInning.Stats> top = new ArrayList<>();
        private final List<HalfInning.Stats> bottom = new ArrayList<>();
        
        public void add(HalfInning.Stats stats) {
            if (top.size() == bottom.size()) {
                top.add(stats);
            } else {
                bottom.add(stats);
            }
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
            } else if (top.size() == 9) {
                return getHomeScore() > getRoadScore();
            } else {
                // extra innings
                return !isInMiddleOf() && (getHomeScore() != getRoadScore());
            }
        }
        
        private boolean isInMiddleOf() {
            return top.size() > bottom.size();
        }
        
        public Optional<Integer> runsNeededToWalkOf() {
            Integer o = (top.size() >= 9) && isInMiddleOf()
                    ? Integer.valueOf(getRoadScore() - getHomeScore() + 1)
                    : null;
            checkState(o == null || o.intValue() > 0);
            return Optional.ofNullable(o);
        }
        
        public boolean isEmpty() {
            return top.isEmpty() && bottom.isEmpty();
        }
    }
    
}
