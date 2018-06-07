package bsbll.game.report;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.game.BoxScore;
import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.report.AbstractPlainTextReport;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
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
        // TODO: Complete me. Batting Events, Pitching Lines, Pitching Events.
        return linesBuilder.build();
    }
    
    private void writeBattingStats(Team team, 
                                   Lineup lineup, 
                                   PlayerGameStats stats, 
                                   ImmutableList.Builder<String> lines) {
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        String header = namePadding.right(team.getName()) +
                pad3.left(BattingStat.AT_BATS.abbrev()) +
                pad3.left(BattingStat.RUNS.abbrev()) +
                pad3.left(BattingStat.HITS.abbrev()) +
                pad4.left(BattingStat.RUNS_BATTED_IN.abbrev());
        lines.add(header);
        for (Player p : lineup.getBattingOrder().getBatters()) {
            BattingStatLine statLine = stats.getBattingLine(p);
            // TODO: Use the player name, once we have it
            String line = namePadding.right(p.getId()) +
                    pad3.left(statLine.get(BattingStat.AT_BATS)) +
                    pad3.left(statLine.get(BattingStat.RUNS)) +
                    pad3.left(statLine.get(BattingStat.HITS)) +
                    pad3.left(statLine.get(BattingStat.RUNS_BATTED_IN));
            lines.add(line);
        }
    }
}
