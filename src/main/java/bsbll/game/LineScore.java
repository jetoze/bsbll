package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.List;

import com.google.common.collect.ImmutableList;

import bsbll.game.HalfInning.Stats;
import bsbll.team.Team;

public final class LineScore {
    private final Line homeLine;
    private final Line visitingLine;
    
    public LineScore(Line homeLine, Line visitingLine) {
        this.homeLine = requireNonNull(homeLine);
        this.visitingLine = requireNonNull(visitingLine);
    }
    
    public Line getHomeLine() {
        return homeLine;
    }

    public Line getVisitingLine() {
        return visitingLine;
    }

    
    public static final class Line {
        private final Team team;
        private final ImmutableList<HalfInning.Stats> innings;
        private final LineSummary summary;
        
        public Line(Team team, List<Stats> innings) {
            this.team = requireNonNull(team);
            this.innings = ImmutableList.copyOf(innings);
            int r = 0;
            int h = 0;
            int e = 0;
            for (Stats s : innings) {
                r += s.getRuns();
                h += s.getHits();
                e += s.getErrors();
            }
            this.summary = new LineSummary(r, h, e);
        }
        
        public Team getTeam() {
            return team;
        }
        
        public ImmutableList<HalfInning.Stats> getInnings() {
            return innings;
        }
        
        public LineSummary getSummary() {
            return summary;
        }
    }
    
    
    public static final class LineSummary {
        private final int runs;
        private final int hits;
        private final int errors;
        
        public LineSummary(int runs, int hits, int errors) {
            this.runs = checkNotNegative(runs);
            this.hits = checkNotNegative(hits);
            this.errors = checkNotNegative(errors);
        }

        public int getRuns() {
            return runs;
        }

        public int getHits() {
            return hits;
        }

        public int getErrors() {
            return errors;
        }
    }
}
