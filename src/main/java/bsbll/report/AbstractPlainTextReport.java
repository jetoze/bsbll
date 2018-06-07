package bsbll.report;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintStream;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.team.TeamName;
import tzeth.strings.Padding;

public abstract class AbstractPlainTextReport<T> {
    private final NameMode mode;
    private final Padding namePad;

    protected AbstractPlainTextReport(NameMode mode) {
        this.mode = requireNonNull(mode);
        this.namePad = Padding.of(getNameWidth(mode));
    }
    
    private static int getNameWidth(NameMode mode) {
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
        return namePad.right(mode.applyTo(name));
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
