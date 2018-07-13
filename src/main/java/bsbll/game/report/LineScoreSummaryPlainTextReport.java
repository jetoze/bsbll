package bsbll.game.report;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.game.LineScore;
import bsbll.game.LineScore.Line;
import bsbll.game.LineScore.LineSummary;
import bsbll.report.AbstractPlainTextReport;
import tzeth.strings.Padding;

/**
 * Writes the summary lines from a {@link LineScore} as plain text.
 */
@Immutable
public final class LineScoreSummaryPlainTextReport extends AbstractPlainTextReport<LineScore> {
    private final NameMode mode;
    private final Padding namePad;
    private final Padding valuePad = Padding.of(3);
    private final String header;
    
    public LineScoreSummaryPlainTextReport(NameMode mode) {
        this.mode = requireNonNull(mode);
        this.namePad = Padding.of(mode.getWidthOfTeamName());
        this.header = createHeader(mode);
    }
    
    private String createHeader(NameMode mode) {
        String rhe = valuePad.left("R") + valuePad.left("H") + valuePad.left("E");
        return (mode == NameMode.NONE)
                ? rhe
                : namePad.right("") + rhe;
    }

    @Override
    public ImmutableList<String> format(LineScore score) {
        return ImmutableList.of(
                header, 
                format(score.getVisitingLine()), 
                format(score.getHomeLine()));
    }
    
    public String format(Line line) {
        String name = namePad.right(mode.applyTo(line.getTeam().getName()));
        LineSummary summary = line.getSummary();
        String statLine = valuePad.left(summary.getRuns()) +
                valuePad.left(line.getSummary().getHits()) +
                valuePad.left(line.getSummary().getErrors());
        return name + statLine;
    }
}
