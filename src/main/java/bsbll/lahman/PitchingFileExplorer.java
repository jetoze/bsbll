package bsbll.lahman;

import static bsbll.stats.PitchingStat.*;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.player.PlayerId;
import bsbll.stats.PitchingStatLine;

public final class PitchingFileExplorer {
    private static final String DEFAULT_LOCATION = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/";

    private final File root;
    
    public PitchingFileExplorer(File root) {
        this.root = requireNonNull(root);
    }

    public static PitchingFileExplorer defaultExplorer() {
        return new PitchingFileExplorer(new File(DEFAULT_LOCATION));
    }
    
    public PitchingStatLine getPlayerStats(PlayerId playerId, Year year) {
        try (Stream<String> stream = openFile(year)) {
            return stream
                    .filter(s -> s.startsWith(playerId.toString()))
                    .map(s -> s.split(",", -1))
                    .map(this::toStats)
                    .reduce(new PitchingStatLine(), PitchingStatLine::plus);
                    
        }
    }
    
    public PlayerCard generatePlayerCard(PlayerId playerId, Year year, PlayerCard leagueCard) {
        try {
            PitchingStatLine stats = getPlayerStats(playerId, year);
            return PlayerCard.of(stats, leagueCard);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to generate pitching card for %s [%s]. Error: %s",
                    playerId, year, e.getMessage()), e);
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
        File parent = new File(root, "pitching");
        return new File(parent, "pitching-" + year + ".csv");
    }

    private PitchingStatLine toStats(String[] line) {
        int bf = toInt(line[24]);
        int outs = toInt(line[12]);
        int hits = toInt(line[13]);
        int homeruns = toInt(line[15]);
        int walks = toInt(line[16]);
        int strikeouts = toInt(line[17]);
        int hitByPitches = toInt(line[22]);
        return PitchingStatLine.builder()
                .set(BATTERS_FACED, bf)
                .set(OUTS, outs)
                .set(HITS, hits)
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
