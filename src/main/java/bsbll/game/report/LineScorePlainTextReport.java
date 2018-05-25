package bsbll.game.report;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.game.HalfInning.Stats;
import bsbll.game.LineScore;
import bsbll.team.TeamName;
import bsbll.team.TeamName.Mode;

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
        boolean previousInningWasWide = false;
        for (int n = 0; n < visitingInnings.size(); ++n) {
            int visitingScore = visitingInnings.get(n).getRuns();
            int homeScore = (n < homeInnings.size())
                    ? homeInnings.get(n).getRuns()
                    : -1;
            boolean wideInning = (visitingScore >= 10) || (homeScore >= 10);
            if ((n > 0) && ((n % 3) == 0)) {
                String space = previousInningWasWide || wideInning
                        ? " "
                        : "  ";
                header.append(space);
                visitingLine.append(space);
                homeLine.append(space);
                previousInningWasWide = false;
            }
            appendInning(header, visitingLine, visitingScore, homeLine, homeScore, previousInningWasWide);
            previousInningWasWide = wideInning;
        }
    }

    private void appendInning(StringBuilder header,
                              StringBuilder visitingLine,
                              int visitingScore,
                              StringBuilder homeLine,
                              int homeScore,
                              boolean previousInningWasWide) {
        if (visitingScore >= 10 || homeScore >= 10) {
            header.append("    ");
        } else {
            if (!previousInningWasWide) {
                header.append(" ");
            }
            header.append(" ");
        }
        appendInning(visitingLine, visitingScore, homeScore >= 10, previousInningWasWide);
        appendInning(homeLine, homeScore, visitingScore >= 10, previousInningWasWide);
    }
    
    private void appendInning(StringBuilder line, 
                              int score, 
                              boolean otherSideScoredDoubleDigits,
                              boolean previousInningWasWide) {
        // TODO: Refactor me!
        if (score >= 10) {
            line.append("(").append(score).append(")");
        } else if (score >= 0) {
            if (otherSideScoredDoubleDigits) {
                line.append("  ").append(score).append(" ");
            } else {
                if (!previousInningWasWide) {
                    line.append(" ");
                }
                line.append(score);
            }
        } else {
            if (otherSideScoredDoubleDigits) {
                line.append("  x ");
            } else {
                if (!previousInningWasWide) {
                    line.append(" ");
                }
                line.append("x");
            }
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
