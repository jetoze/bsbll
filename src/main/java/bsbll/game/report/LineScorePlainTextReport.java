package bsbll.game.report;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.game.HalfInning.Stats;
import bsbll.game.LineScore;
import bsbll.team.TeamName;
import bsbll.team.TeamName.Mode;
import tzeth.strings.Padding;

/**
 * Writes a line score as plain text.
 */
public final class LineScorePlainTextReport extends AbstractPlainTextReport<LineScore> {
    
    public LineScorePlainTextReport(TeamName.Mode mode) {
        super(mode);
    }

    @Override
    public ImmutableList<String> format(LineScore score) {
        StringBuilder header = new StringBuilder(getNamePadding().padRight(""));
        StringBuilder visitingLine = new StringBuilder(getTeamName(score.getVisitingLine().getTeam().getName()));
        StringBuilder homeLine = new StringBuilder(getTeamName(score.getHomeLine().getTeam().getName()));
        appendInnings(score, header, visitingLine, homeLine);
        appendSummary(score, header, visitingLine, homeLine);
        return ImmutableList.of(header.toString(), visitingLine.toString(), homeLine.toString());
    }
    
    private void appendInnings(LineScore score,
                               StringBuilder header,
                               StringBuilder visitingLine,
                               StringBuilder homeLine) {
        ImmutableList<Stats> visitingInnings = score.getVisitingLine().getInnings();
        ImmutableList<Stats> homeInnings = score.getHomeLine().getInnings();
        Padding singleDigitPadding = Padding.of(2);
        Padding doubleDigitPadding = Padding.of(4);
        for (int n = 0; n < visitingInnings.size(); ++n) {
            if ((n > 0) && ((n % 3) == 0)) {
                String space = "  ";
                header.append(space);
                visitingLine.append(space);
                homeLine.append(space);
            }
            // FIXME: The formatting when one side has a double-digit inning
            // is broken. In that case we want the other side's value to be
            // centered, not left-padded.
            int visitingScore = visitingInnings.get(n).getRuns();
            int homeScore = (n < homeInnings.size())
                    ? homeInnings.get(n).getRuns()
                    : -1;
            Padding padding = ((visitingScore >= 10) || (homeScore >= 10))
                    ? doubleDigitPadding
                    : singleDigitPadding;
            header.append(padding.padRight(""));
            visitingLine.append(padding.padLeft(runsAsString(visitingScore)));
            homeLine.append(padding.padLeft(runsAsString(homeScore)));
        }
    }

    private static String runsAsString(int runs) {
        if (runs >= 10) {
            return "(" + runs + ")";
        } else if (runs >= 0) {
            return Integer.toString(runs);
        } else {
            return "x";
        }
    }
    
    private void appendSummary(LineScore score, 
                               StringBuilder header, 
                               StringBuilder visitingLine,
                               StringBuilder homeLine) {
        String div = "  -";
        header.append(Strings.repeat(" ", div.length()));
        visitingLine.append(div);
        homeLine.append(div);
        ImmutableList<String> summaryLines = new LineScoreSummaryPlainTextReport(Mode.NONE).format(score);
        header.append(summaryLines.get(0));
        visitingLine.append(summaryLines.get(1));
        homeLine.append(summaryLines.get(2));
    }
}
