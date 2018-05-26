package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class PlayByPlayFile {
    private final Path path;
    
    public static PlayByPlayFile of(String path) {
        return new PlayByPlayFile(new File(path));
    }
    
    public PlayByPlayFile(File file) {
        this(file.toPath());
    }
    
    public PlayByPlayFile(Path path) {
        this.path = requireNonNull(path);
    }
    
    public static Stream<PlayByPlayFile> stream(File folder) {
        checkArgument(folder.isDirectory(), "No such directory: " + folder.getAbsolutePath());
        FileFilter filter = f -> {
            String name = f.getName();
            return name.endsWith(".EVA") || name.endsWith("EVN") || name.endsWith(".EDN");
        };
        File[] files = folder.listFiles(filter);
        return (files == null)
                ? Stream.of()
                : Arrays.stream(files).map(PlayByPlayFile::new);
    }
    
    public static void parseAll(File folder, Callback callback) {
        stream(folder).forEach(f -> f.parse(callback));
    }

    public void parse(Callback callback) {
        requireNonNull(callback);
        try {
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(new LineParser(callback));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class LineParser implements Consumer<String> {
        private final Callback callback;
        private Inning inning;
        
        public LineParser(Callback callback) {
            this.callback = callback;
        }
        
        @Override
        public void accept(String line) {
            if (line.startsWith("id,")) {
                inning = new Inning(1, Inning.TOP);
                callback.onStartGame();
            } else if (line.startsWith("play,")) {
                parsePlay(line);
            }
        }
        
        private void parsePlay(String line) {
            String[] parts = line.split(",");
            checkInning(parts);
            parseEventField(parts);
        }

        private void checkInning(String[] parts) {
            int inningNo = Integer.parseInt(parts[1].trim());
            boolean newInning = (inningNo > this.inning.getNumber()) ||
                    (parts[2].trim().equals("1") && this.inning.getHalf() == Inning.TOP);
            if (newInning) {
                this.inning = inning.next();
                callback.onStartInning(this.inning);
            }
        }
        
        private void parseEventField(String[] parts) {
            String rawEventField = parts[6].trim();
            if (!callback.rawEventFieldFilter().test(rawEventField)) {
                return;
            }
            EventField field = EventField.fromString(rawEventField);
            PlayOutcome outcome = EventParser.parse(field);
            if (callback.outcomeFilter().test(outcome)) {
                callback.onEvent(field, outcome);
            }
        }
    }
    
    
    
    public static interface Callback {
        default void onStartGame(/*TODO: Pass in game info*/) {/**/}
        default void onStartInning(Inning inning) {/**/}
        default Predicate<String> rawEventFieldFilter() {
            return s -> true;
        }
        default Predicate<PlayOutcome> outcomeFilter() {
            return o -> true;
        }
        default void onEvent(EventField field, PlayOutcome outcome) {/**/}
    }
    
    
    public static final class Inning {
        
        public static enum Half { TOP, BOTTOM };
        
        public static final Half TOP = Half.TOP;
        public static final Half BOTTOM = Half.BOTTOM;
        
        private final int number;
        private final Half half;

        public Inning(int number, Half half) {
            this.number = checkPositive(number);
            this.half = requireNonNull(half);
        }
        
        public int getNumber() {
            return number;
        }

        public Half getHalf() {
            return half;
        }

        public Inning next() {
            switch (half) {
            case TOP:
                return new Inning(number, BOTTOM);
            case BOTTOM:
                return new Inning(number + 1, TOP);
            default:
                throw new AssertionError(half);
            }
        }
    }
    
    public static enum InningHalf {
        TOP, BOTTOM
    }
}
