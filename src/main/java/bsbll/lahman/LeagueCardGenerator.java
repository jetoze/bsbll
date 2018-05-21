package bsbll.lahman;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.stats.BattingStats;

public class LeagueCardGenerator {
    // The Lahman DB can be downloaded from here:
    //    http://www.seanlahman.com/baseball-database.html
    
    private static final String ROOT = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/";
    
    private final File root;
    
    public LeagueCardGenerator(File root) {
        this.root = requireNonNull(root);
    }
    
    public static LeagueCardGenerator defaultGenerator() {
        return new LeagueCardGenerator(new File(ROOT));
    }

    public PlayerCard generateCard(LeagueId league, Year year) throws IOException {
        return readFromBattingFile(league, year);
    }

    private PlayerCard readFromBattingFile(LeagueId league, Year year) throws IOException {
        File battingFile = new File(root, "Batting.csv");
        BattingStats stats = Files.lines(battingFile.toPath())
                .map(s -> s.split(",", -1))
                .filter(s -> filterBattingLine(s, league, year))
                .map(this::readStatsFromBattingFile)
                .reduce(new BattingStats(), BattingStats::add);
        return PlayerCard.of(stats);
    }
    
    private boolean filterBattingLine(String[] parts, LeagueId league, Year year) {
        return parts[1].equals(year.toString()) && parts[4].equals(league.name());
    }
    
    private BattingStats readStatsFromBattingFile(String[] parts) {
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
    
    private PlayerCard readFromTeamsFile(LeagueId league, Year year) throws IOException {
        File teamsFile = new File(root, "Teams.csv");
        String preAmble = year + "," + league.name();
        BattingStats stats = Files.lines(teamsFile.toPath())
                .filter(s -> s.startsWith(preAmble))
                .map(this::readStatsFromTeamsFile)
                .reduce(new BattingStats(), BattingStats::add);
        return PlayerCard.of(stats);
    }
    
    private BattingStats readStatsFromTeamsFile(String line) {
        String[] parts = line.split(",", -1);
        int atBats = toInt(parts[15]);
        int hits = toInt(parts[16]);
        int doubles = toInt(parts[17]);
        int triples = toInt(parts[18]);
        int homeruns = toInt(parts[19]);
        int walks = toInt(parts[20]);
        int strikeouts = toInt(parts[21]);
        int hitByPitches = toInt(parts[24]);
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
