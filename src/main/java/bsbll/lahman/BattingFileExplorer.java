package bsbll.lahman;

import static bsbll.stats.BattingStat.*;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStatLine;

public final class BattingFileExplorer {
    private static final String DEFAULT_LOCATION = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/";

    private final File root;
    
    public BattingFileExplorer(File root) {
        this.root = requireNonNull(root);
    }

    public static BattingFileExplorer defaultExplorer() {
        return new BattingFileExplorer(new File(DEFAULT_LOCATION));
    }
    
    public BattingStatLine getPlayerStats(PlayerId playerId, Year year) {
        try (Stream<String> stream = openFile(year)) {
            return createStats(stream
                    .filter(s -> s.startsWith(playerId.toString()))
                    .map(s -> s.split(",", -1)));
        }
    }
    
    private BattingStatLine createStats(Stream<String[]> stream) {
        return stream.map(this::toStats).reduce(new BattingStatLine(), BattingStatLine::plus);
    }
    
    public PlayerCard generatePlayerCard(PlayerId playerId, Year year) {
        try {
            BattingStatLine stats = getPlayerStats(playerId, year);
            return PlayerCard.of(stats);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to generate batting card for %s [%s]. Error: %s",
                    playerId, year, e.getMessage()), e);
        }
    }
    
    public BattingStatLine getLeagueStats(LeagueId leagueId, Year year) {
        try (Stream<String> stream = openFile(year)) {
            return createStats(stream
                    .map(s -> s.split(",", -1))
                    .filter(a -> a[4].equals(leagueId.name())));
        }
    }
    
    public PlayerCard generateLeagueCard(LeagueId leagueId, Year year) {
        try {
            BattingStatLine stats = getLeagueStats(leagueId, year);
            return PlayerCard.of(stats);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to generate league card for %s [%s]. Error: %s",
                    leagueId, year, e.getMessage()), e);
        }
    }
    
    private Stream<String> openFile(Year year) {
        try {
            File file = getFile(year);
            return Files.lines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private File getFile(Year year) {
        File parent = new File(root, "batting");
        return new File(parent, "batting-" + year + ".csv");
    }
    
    private BattingStatLine toStats(String[] parts) {
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
        return BattingStatLine.builder()
                .set(PLATE_APPEARANCES, plateAppearances)
                .set(HITS, hits)
                .set(DOUBLES, doubles)
                .set(TRIPLES, triples)
                .set(HOMERUNS, homeruns)
                .set(WALKS, walks)
                .set(STRIKEOUTS, strikeouts)
                .set(HIT_BY_PITCHES, hitByPitches)
                .build();
    }
    
    private static int toInt(String s) {
        return s.isEmpty()
                ? 0
                : Integer.parseInt(s);
    }
}
