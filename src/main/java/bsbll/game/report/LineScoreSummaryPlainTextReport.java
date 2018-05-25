package bsbll.game.report;

import com.google.common.collect.ImmutableList;

import bsbll.game.LineScore;
import bsbll.game.LineScore.Line;
import bsbll.game.LineScore.LineSummary;
import bsbll.team.TeamName;
import tzeth.strings.Padding;

/**
 * Writes the summary lines from a {@link LineScore} as plain text.
 */
public final class LineScoreSummaryPlainTextReport extends AbstractPlainTextReport<LineScore> {
    private final Padding valuePad = Padding.of(3);
    private final String header;
    
    public LineScoreSummaryPlainTextReport(TeamName.Mode mode) {
        super(mode);
        this.header = createHeader(mode);
    }
    
    private String createHeader(TeamName.Mode mode) {
        String rhe = valuePad.padLeft("R") + valuePad.padLeft("H") + valuePad.padLeft("E");
        return (mode == TeamName.Mode.NONE)
                ? rhe
                : getNamePadding().padRight("") + rhe;
    }

    @Override
    public ImmutableList<String> format(LineScore score) {
        return ImmutableList.of(
                header, 
                format(score.getVisitingLine()), 
                format(score.getHomeLine()));
    }
    
    public String format(Line line) {
        String name = getTeamName(line.getTeam().getName());
        LineSummary summary = line.getSummary();
        String statLine = valuePad.padLeft(summary.getRuns()) +
                valuePad.padLeft(line.getSummary().getHits()) +
                valuePad.padLeft(line.getSummary().getErrors());
        return name + statLine;
    }
}
