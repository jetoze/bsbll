package bsbll.game.report;

import java.io.IOException;
import java.io.PrintStream;

import com.google.common.collect.ImmutableList;

import bsbll.game.LineScore;
import bsbll.game.LineScore.Line;
import bsbll.game.LineScore.LineSummary;
import bsbll.team.TeamName;
import tzeth.strings.Padding;

public final class LineScoreSummaryPlainTextReport {
    private final TeamNameMode mode;
    private final Padding namePad;
    private final Padding valuePad = Padding.of(3);
    private final String header;
    
    public LineScoreSummaryPlainTextReport(TeamNameMode mode) {
        this.mode = mode;
        this.namePad = mode.getPadding();
        this.header = createHeader();
    }
    
    private String createHeader() {
        String rhe = valuePad.padLeft("R") + valuePad.padLeft("H") + valuePad.padLeft("E");
        return (mode == TeamNameMode.NONE)
                ? rhe
                : namePad.padRight("") + rhe;
    }

    public ImmutableList<String> format(LineScore score) {
        return ImmutableList.of(
                header, 
                format(score.getVisitingLine()), 
                format(score.getHomeLine()));
    }
    
    public String format(Line line) {
        String name = namePad.padRight(
                mode.getTeamNameRepresentation(line.getTeam().getName()));
        LineSummary summary = line.getSummary();
        String statLine = valuePad.padLeft(summary.getRuns()) +
                valuePad.padLeft(line.getSummary().getHits()) +
                valuePad.padLeft(line.getSummary().getErrors());
        return name + statLine;
    }
    
    // TODO: The following writeTo/appendTo method will probably be common with other
    // report classes. Once we have them, create a common abstract base-class and move
    // them there.

    public void writeTo(LineScore score, PrintStream out) {
        format(score).forEach(out::println);
    }
    
    public void appendTo(LineScore score, Appendable out) throws IOException {
        for (String line : format(score)) {
            out.append(line);
            out.append(System.lineSeparator());
        }
    }
    
    public void appendTo(LineScore score, StringBuilder sb) {
        try {
            appendTo(score, (Appendable) sb);
        } catch (IOException e) {
            // StringBuilder::append never throws
            throw new AssertionError();
        }
    }
    
    // TODO: Lift out the defaultWidth field (which is context dependent) and move this enum
    // to bsbll.team.
    public static enum TeamNameMode {
        NONE(0),
        MAIN(14),
        FULL(24),
        ABBREV(6);
        
        private final int defaultWidth;
        
        private TeamNameMode(int defaultWidth) {
            this.defaultWidth = defaultWidth;
        }
        
        public final Padding getPadding() {
            return Padding.of(defaultWidth);
        }
        
        public final String getTeamNameRepresentation(TeamName name) {
            switch (this) { 
            case NONE:
                return "";
            case MAIN:
                return name.getMainName();
            case FULL:
                return name.getFullName();
            case ABBREV:
                return name.getAbbreviation();
            default:
                throw new AssertionError("Unknown TeamNameMode: " + this);
            }
        }
        
    }
}
