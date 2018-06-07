package bsbll.game.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import bsbll.NameMode;
import bsbll.game.BoxScore;
import bsbll.game.ExtraBaseHitEvent;
import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.report.AbstractPlainTextReport;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStatLine;
import bsbll.team.Lineup;
import bsbll.team.Team;
import tzeth.strings.Padding;

public class BoxScorePlainTextReport extends AbstractPlainTextReport<BoxScore> {
    public BoxScorePlainTextReport() {
    }
    
    @Override
    public ImmutableList<String> format(BoxScore boxScore) {
        ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
        
        LineScorePlainTextReport lineScoreReport = new LineScorePlainTextReport(NameMode.ABBREV);
        linesBuilder.addAll(lineScoreReport.format(boxScore.getLineScore()));
        linesBuilder.add("");
        
        writeBattingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writeBattingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        
        EventReporter events = new EventReporter(boxScore);
        linesBuilder.addAll(events.getBattingEventLines());
        linesBuilder.add("");
        
        writePitchingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writePitchingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        
        // TODO: Pitching Events go here.
        
        return linesBuilder.build();
    }
    
    private void writeBattingStats(Team team, 
                                   Lineup lineup, 
                                   PlayerGameStats stats, 
                                   ImmutableList.Builder<String> lines) {
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        // TODO: Between the header, the player lines, and the Totals line, we
        // do the same padding + concatenation logic in three places. Come up
        // with a way to do this once only.
        lines.add(namePadding.right(team.getName()) +
                pad3.left(BattingStat.AT_BATS.abbrev()) +
                pad3.left(BattingStat.RUNS.abbrev()) +
                pad3.left(BattingStat.HITS.abbrev()) +
                pad4.left(BattingStat.RUNS_BATTED_IN.abbrev()));
        BattingStatLine totals = new BattingStatLine();
        for (Player p : lineup.getBattingOrder().getBatters()) {
            BattingStatLine statLine = stats.getBattingLine(p);
            // TODO: Use the player name, once we have it
            lines.add(namePadding.right(p.getId()) +
                    pad3.left(statLine.get(BattingStat.AT_BATS)) +
                    pad3.left(statLine.get(BattingStat.RUNS)) +
                    pad3.left(statLine.get(BattingStat.HITS)) +
                    pad4.left(statLine.get(BattingStat.RUNS_BATTED_IN)));
            totals = totals.plus(statLine);
        }
        lines.add(namePadding.right("Totals:") +
                    pad3.left(totals.get(BattingStat.AT_BATS)) +
                    pad3.left(totals.get(BattingStat.RUNS)) +
                    pad3.left(totals.get(BattingStat.HITS)) +
                    pad4.left(totals.get(BattingStat.RUNS_BATTED_IN)));
    }
    
    private void writePitchingStats(Team team, 
                                    Lineup lineup, 
                                    PlayerGameStats stats, 
                                    ImmutableList.Builder<String> lines) {
        // TODO: Winning and losing pitchers, and their new records.
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        lines.add(namePadding.right(team.getName()) +
                pad4.left(PitchingStat.INNINGS_PITCHED.abbrev()) +
                pad3.left(PitchingStat.HITS.abbrev()) +
                pad3.left(PitchingStat.RUNS.abbrev()) +
                pad3.left(PitchingStat.EARNED_RUNS.abbrev()) +
                pad3.left(PitchingStat.WALKS.abbrev()) +
                pad3.left(PitchingStat.STRIKEOUTS.abbrev()));
        // TODO: This will obviously change once we implement pitcher substitutions.
        Player pitcher = lineup.getPitcher();
        PitchingStatLine statLine = stats.getPitchingLine(pitcher);
        // TODO: Use the player name, once we have it
        lines.add(namePadding.right(pitcher) +
                pad4.left(statLine.get(PitchingStat.INNINGS_PITCHED)) +
                pad3.left(statLine.get(PitchingStat.HITS)) +
                pad3.left(statLine.get(PitchingStat.RUNS)) +
                pad3.left(statLine.get(PitchingStat.EARNED_RUNS)) +
                pad3.left(statLine.get(PitchingStat.WALKS)) +
                pad3.left(statLine.get(PitchingStat.STRIKEOUTS)));
    }
    
    
    private static class EventReporter {
        private static final int MAX_WIDTH = 76;
        private final BoxScore boxScore;
        
        public EventReporter(BoxScore boxScore) {
            this.boxScore = boxScore;
        }
        
        public List<String> getBattingEventLines() {
            List<String> lines = new ArrayList<>();
            lines.addAll(getExtraBaseHits(boxScore.getGameEvents().getDoubles(), BattingStat.DOUBLES.abbrev()));
            lines.addAll(getExtraBaseHits(boxScore.getGameEvents().getTriples(), BattingStat.TRIPLES.abbrev()));
            // TODO: Add homeruns
            return lines;
        }
        
        private List<String> getExtraBaseHits(ImmutableList<? extends ExtraBaseHitEvent> xbhs, String abbrev) {
            if (xbhs.isEmpty()) {
                return Collections.emptyList();
            }
            Multimap<Team, ? extends ExtraBaseHitEvent> byTeam = Multimaps.index(xbhs, x -> boxScore.getTeam(x.getBatter()));
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder(abbrev).append(": ");
            line = addExtraBaseHits(boxScore.getVisitingTeam(), byTeam.get(boxScore.getVisitingTeam()), line, lines);
            line = addExtraBaseHits(boxScore.getHomeTeam(), byTeam.get(boxScore.getHomeTeam()), line, lines);
            // Replace the last "; " with a "."
            line.replace(line.length() - 2, line.length(), ".");
            lines.add(line.toString());
            return lines;
        }
        
        private StringBuilder addExtraBaseHits(Team team, 
                                               Collection<? extends ExtraBaseHitEvent> xbhs,
                                               StringBuilder line,
                                               List<String> lines) {
            if (xbhs.isEmpty()) {
                return line;
            }
            line.append(team.getName().getMainName()).append(" ");
            for (ExtraBaseHitEvent xbh : xbhs) {
                String e = xbhToString(xbh);
                if ((line.length() + e.length()) > MAX_WIDTH) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(e);
            }
            return line;
        }
        
        private String xbhToString(ExtraBaseHitEvent xbh) {
            // TODO: Add season totals. And make it configurable if the pitcher should
            // be included.
            return String.format("%s (off %s); ", xbh.getBatter().getId(), xbh.getPitcher().getId());
        }
    }
}
