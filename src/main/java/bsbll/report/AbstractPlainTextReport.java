package bsbll.report;

import java.io.IOException;
import java.io.PrintStream;

import com.google.common.collect.ImmutableList;

public abstract class AbstractPlainTextReport<T> {

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
