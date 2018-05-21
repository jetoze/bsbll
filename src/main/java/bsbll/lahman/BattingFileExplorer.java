package bsbll.lahman;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStats;

public final class BattingFileExplorer {
    private static final String DEFAULT_LOCATION = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/batting";

    private final File root;
    
    public BattingFileExplorer(File root) {
        this.root = requireNonNull(root);
    }

    public static BattingFileExplorer defaultExplorer() {
        return new BattingFileExplorer(new File(DEFAULT_LOCATION));
    }
    
    public PlayerCard generatePlayerCard(PlayerId playerId, Year year) {
        try {
            File file = new File(root, "batting-" + year + ".csv");
            Stream<String[]> stream = Files.lines(file.toPath())
                    .filter(s -> s.startsWith(playerId.toString()))
                    .map(s -> s.split(",", -1));
            return createCard(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public PlayerCard generateLeagueCard(LeagueId leagueId, Year year) {
        try {
            File file = new File(root, "batting-" + year + ".csv");
            Stream<String[]> stream = Files.lines(file.toPath())
                    .map(s -> s.split(",", -1))
                    .filter(a -> a[4].equals(leagueId.name()));
            return createCard(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private PlayerCard createCard(Stream<String[]> stream) {
        BattingStats stats = stream.map(this::toStats)
                .reduce(new BattingStats(), BattingStats::add);
        return PlayerCard.of(stats);
    }
    
    private BattingStats toStats(String[] parts) {
        int atBats = toInt(parts[6]);
        int hits = toInt(parts[8]);
        int doubles = toInt(parts[9]);
        int triples = toInt(parts[10]);
        int homeruns = toInt(parts[11]);
        int walks = toInt(parts[15]) + toInt(parts[17]);
        int strikeouts = toInt(parts[16]);
        int hitByPitches = toInt(parts[18]);
        int sacrificeHits = toInt(parts[19]);
        int sacrificeFlies = toInt(parts[20]);
        // TODO: What about interference calls? Probably negligible.
        int plateAppearances = atBats + walks + hitByPitches + sacrificeHits + sacrificeFlies;
        return new BattingStats(
                plateAppearances,
                hits,
                doubles,
                triples,
                homeruns,
                walks,
                strikeouts,
                hitByPitches);
    }
    
    private static int toInt(String s) {
        return s.isEmpty()
                ? 0
                : Integer.parseInt(s);
    }

}
