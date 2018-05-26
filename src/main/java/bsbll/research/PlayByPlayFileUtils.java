package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import bsbll.Year;

final class PlayByPlayFileUtils {
    public static File[] listFiles(Year year) {
        File rootDir = new File("/Users/torgil/coding/data/bsbll/play-by-play-files/" + year);
        checkArgument(rootDir.isDirectory(), "No such directory: " + rootDir.getAbsolutePath());
        FileFilter filter = f -> {
            String name = f.getName();
            return name.endsWith(".EVA") || name.endsWith("EVN") || name.endsWith(".EDN");
        };
        return rootDir.listFiles(filter);
    }

    public static void parseAllPlays(Year year, Consumer<PlayOutcome> callback) throws Exception {
        parseAllPlays(year, t -> true, callback);
    }
    
    public static void parseAllPlays(Year year, 
                                     Predicate<EventType> typeFilter, 
                                     Consumer<PlayOutcome> callback) throws Exception {
        for (File f : listFiles(year)) {
            try (Stream<String> lines = Files.lines(f.toPath())) {
                lines.filter(s -> s.startsWith("play"))
                    .map(s -> getPlayField(s))
                    .filter(Objects::nonNull)
                    .map(EventField::fromString)
                    .map(EventParser::parse)
                    .filter(o -> typeFilter.test(o.getType()))
                    .forEach(callback);
            }
        }
    }

    public static void collectPlays(Year year, 
                                    Predicate<String> lineFilter, 
                                    Consumer<String> callback) throws Exception {
        for (File f : listFiles(year)) {
            try (Stream<String> lines = Files.lines(f.toPath())) {
                lines.filter(s -> s.startsWith("play"))
                    .map(s -> getPlayField(s))
                    .filter(Objects::nonNull)
                    .filter(lineFilter)
                    .forEach(callback);
            }
        }
    }
    
    @Nullable
    private static String getPlayField(String line) {
        String[] parts = line.split(",");
        return (parts.length >= 7)
                ? parts[6].trim()
                : null;
    }

    private PlayByPlayFileUtils() {/**/}

}
