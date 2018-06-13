package bsbll.research.pbpf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Predicate;

import bsbll.Year;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Callback;

public final class PlayByPlayFileUtils {
    public static File getFolder(Year year) {
        return new File("/Users/torgil/coding/data/bsbll/play-by-play-files/" + year);
    }

    public static void collectPlays(Year year, 
                                    Predicate<String> lineFilter, 
                                    Consumer<String> consumer) throws Exception {
        requireNonNull(lineFilter);
        File folder = getFolder(year);
        PlayByPlayFile.parseAll(folder, new Callback() {

            @Override
            public Predicate<String> rawEventFieldFilter() {
                return lineFilter;
            }

            @Override
            public void onEvent(EventField field, PlayOutcome outcome) {
                consumer.accept(field.getRawString());
            }
        });
    }

    private PlayByPlayFileUtils() {/**/}

}
