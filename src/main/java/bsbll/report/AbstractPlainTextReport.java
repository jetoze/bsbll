package bsbll.report;

import java.io.IOException;
import java.io.PrintStream;

import com.google.common.collect.ImmutableList;

import bsbll.team.TeamName;
import tzeth.strings.Padding;

public abstract class AbstractPlainTextReport<T> {
    // TODO: Eventually we will generate reports that do not include team names,
    // so the TeamName.Mode does not belong here.
    private final TeamName.Mode mode;
    private final Padding namePad;

    protected AbstractPlainTextReport(TeamName.Mode mode) {
        this.mode = mode;
        this.namePad = Padding.of(getNameWidth(mode));
    }
    
    private static int getNameWidth(TeamName.Mode mode) {
        switch (mode) {
        case NONE:
            return 0;
        case ABBREV:
            return 6;
        case MAIN:
            return 14;
        case FULL:
            return 22;
        default:
            throw new AssertionError("Unknown mode: " + mode);
        }
    }
    
    protected final Padding getNamePadding() {
        return namePad;
    }
    
    protected final String getTeamName(TeamName name) {
        return namePad.right(mode.apply(name));
    }

    public abstract ImmutableList<String> format(T t);
    
    public final void writeTo(T t, PrintStream out) {
        format(t).forEach(out::println);
    }
    
    public final void appendTo(T t, Appendable out) throws IOException {
        for (String line : format(t)) {
            out.append(line);
            out.append(System.lineSeparator());
        }
    }
    
    public final void appendTo(T t, StringBuilder sb) {
        try {
            appendTo(t, (Appendable) sb);
        } catch (IOException e) {
            // StringBuilder::append never throws
            throw new AssertionError();
        }
    }

}
