package bsbll.game.report;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.game.BoxScore;
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
        
        // TODO: Batting Events go here.
        
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
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        lines.add(namePadding.right(team.getName()) +
                pad4.left(PitchingStat.INNINGS_PITCHED.abbrev()) +
                pad3.left(PitchingStat.HITS.abbrev()) +
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
                pad3.left(statLine.get(PitchingStat.EARNED_RUNS)) +
                pad3.left(statLine.get(PitchingStat.WALKS)) +
                pad3.left(statLine.get(PitchingStat.STRIKEOUTS)));
    }
}
