package bsbll.lahman;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStats;

public final class BattingFileExplorer {
    private static final String DEFAULT_LOCATION = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/Batting.csv";

    private final File file;
    
    public BattingFileExplorer(File file) {
        this.file = requireNonNull(file);
    }

    public static BattingFileExplorer defaultExplorer() {
        return new BattingFileExplorer(new File(DEFAULT_LOCATION));
    }
    
    public PlayerCard generatePlayerCard(PlayerId playerId, Year year) {
        String prefix = playerId + "," + year;
        try {
            BattingStats stats = Files.lines(file.toPath())
                    .filter(s -> s.startsWith(prefix))
                    .map(s -> s.split(",", -1))
                    .map(this::toStats)
                    .reduce(new BattingStats(), BattingStats::add);
            return PlayerCard.of(stats);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public PlayerCard generateLeagueCard(LeagueId leagueId, Year year) {
        try {
            BattingStats stats = Files.lines(file.toPath())
                    .map(s -> s.split(",", -1))
                    .filter(s -> leagueFilter(s, leagueId, year))
                    .map(this::toStats)
                    .reduce(new BattingStats(), BattingStats::add);
            return PlayerCard.of(stats);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean leagueFilter(String[] parts, LeagueId leagueId, Year year) {
        return parts[1].equals(year.toString()) && parts[4].equals(leagueId.name());
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
