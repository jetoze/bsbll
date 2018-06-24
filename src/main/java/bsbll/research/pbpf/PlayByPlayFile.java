package bsbll.research.pbpf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
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

import bsbll.game.play.PlayOutcome;
import bsbll.player.PlayerId;
import bsbll.research.EventField;
import bsbll.research.EventParser;

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
    
    public Path getPath() {
        return path;
    }
    
    public String getName() {
        return path.getFileName().toString();
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
    
    @Override
    public String toString() {
        return path.toString();
    }
    
    
    private static class LineParser implements Consumer<String> {
        private final Callback callback;
        private Inning inning;
        private PlayerId homePitcher;
        private PlayerId visitingPitcher;
        
        public LineParser(Callback callback) {
            this.callback = callback;
        }
        
        @Override
        public void accept(String line) {
            if (line.startsWith("id,")) {
                inning = new Inning(1, Inning.TOP);
                callback.onStartGame(line.split(",")[1]);
                callback.onStartInning(inning);
            } else if (line.startsWith("start,") || line.startsWith("sub,")) {
                extractPitcherInfo(line);
            } else if (line.startsWith("play,")) {
                parsePlay(line);
            }
        }
        
        private void extractPitcherInfo(String line) {
            String[] parts = line.split(",");
            if (parts[5].equals("1")) {
                PlayerId id = PlayerId.of(parts[1]);
                if (parts[2].equals("0")) {
                    visitingPitcher = id;
                } else {
                    homePitcher = id;
                }
            }
        }
        
        private void parsePlay(String line) {
            checkState(this.inning != null);
            String[] parts = line.split(",");
            checkInning(parts);
            parseEventField(parts);
        }

        private void checkInning(String[] parts) {
            int inningNo = Integer.parseInt(parts[1].trim());
            boolean newInning = (inningNo > this.inning.getNumber()) ||
                    (parts[2].trim().equals("1") && this.inning.getHalf() == Inning.TOP);
            if (newInning) {
                checkState((this.inning.getHalf() == Inning.TOP && this.inning.getNumber() == inningNo) ||
                        (this.inning.getHalf() == Inning.BOTTOM && inningNo == (this.inning.getNumber() + 1)));
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
                PlayerId batter = PlayerId.of(parts[3]);
                PlayerId pitcher = getPitcher();
                ParsedPlay parsedPlay = new ParsedPlay(batter, pitcher, field, outcome); 
                callback.onEvent(parsedPlay);
            }
        }
        
        private PlayerId getPitcher() {
            // TODO: This may not be correct in some games from the 1800s, where the 
            // home team batted first.
            return inning.isTop()
                    ? visitingPitcher
                    : homePitcher;
        }
    }
    
    
    
    public static interface Callback {
        default void onStartGame(String id) {/**/}
        
        default void onStartInning(Inning inning) {/**/}
        
        default Predicate<String> rawEventFieldFilter() {
            return s -> true;
        }
        
        default Predicate<PlayOutcome> outcomeFilter() {
            return o -> true;
        }
        
        default void onEvent(ParsedPlay parsedPlay) {/**/}
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
        
        public boolean isTop() {
            return half == TOP;
        }
        
        public boolean isBottom() {
            return half == BOTTOM;
        }

        public Inning next() {
            return isTop()
                    ? new Inning(number, BOTTOM)
                    : new Inning(number + 1, TOP);
        }
        
        public Inning previous() {
            checkState(isBottom() || number > 1);
            return isBottom()
                    ? new Inning(number, TOP)
                    : new Inning(number - 1, BOTTOM);
        }
        
        public boolean isWalkOffPossible() {
            return isBottom() && (number >= 9);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(half == TOP ? "Top" : "Bottom").append(" of ");
            String no;
            switch (number) {
            case 1:
                no = "1st";
                break;
            case 2:
                no = "2nd";
                break;
            case 3:
                no = "3rd";
                break;
            default:
                no = number + "th";
                break;
            }
            return sb.append(no).toString();
        }
    }
    
    public static enum InningHalf {
        TOP, BOTTOM
    }
}
