package bsbll.lahman;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.nio.file.Files;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.stats.BattingStats;

public class LeagueCardGenerator {
    private static final String TEAMS_FILE = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/Teams.csv";
    
    private final File teamsFile;
    
    public LeagueCardGenerator(File teamsFile) {
        this.teamsFile = requireNonNull(teamsFile);
    }
    
    public static LeagueCardGenerator defaultGenerator() {
        return new LeagueCardGenerator(new File(TEAMS_FILE));
    }

    public PlayerCard generateCard(LeagueId league, Year year) throws Exception {
        String preAmble = year + "," + league.name();
        BattingStats stats = Files.lines(teamsFile.toPath())
                .filter(s -> s.startsWith(preAmble))
                .map(this::readStats)
                .reduce(new BattingStats(), BattingStats::add);
        return PlayerCard.of(stats);
    }
    
    private BattingStats readStats(String line) {
        String[] parts = line.split(",");
        int atBats = toInt(parts[15]);
        int hits = toInt(parts[16]);
        int doubles = toInt(parts[17]);
        int triples = toInt(parts[18]);
        int homeruns = toInt(parts[19]);
        int walks = toInt(parts[20]);
        int strikeouts = toInt(parts[21]);
        int hitByPitches = toInt(parts[24]);
        if (hitByPitches == 0) {
            // TODO: Generate a number here
        }
        int sacrificeFlies = toInt(parts[25]);
        // TODO: What about sacrifice bunts, and interference calls?
        int plateAppearances = atBats + walks + hitByPitches + sacrificeFlies;
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
