package bsbll.stats;

public enum Batting {
    // TODO: Naming problem. Not only is BattingStat/BattingStats easy to mixup, there's also a 
    // semantic collision: The class BattingStats represents a collection of batting stats with 
    // their values. That would suggest that "BattingStat" is a single stat with a value.
    // Or vice versa, if "BattingStat" is the enum of the various individual stats, then
    // "BattingStats" sounds like it would be a collection of "BattingStat" instances,
    // i.e. without their corresponding values.
    // Suggestions:
    //    + simply "Batting"?
    
    GAMES,
    GAMES_STARTED, // ? unorthodox stat for batting
    PLATE_APPEARANCES,
    AT_BATS(true),
    HITS,
    SINGLES(true),
    DOUBLES,
    TRIPLES,
    HOMERUNS,
    STRIKEOUTS,
    WALKS,
    RUNS,
    RUNS_BATTED_IN,
    HIT_BY_PITCHES,
    SACRIFICE_HITS,
    SACRIFICE_FLIES,
    EXTRA_BASE_HITS(true),
    TOTAL_BASES(true),
    BATTING_AVERAGE(true),
    SLUGGING_PERCENTAGE(true),
    ON_BASE_PERCENTAGE(true),
    OPS(true);
    
    private boolean derived;
    
    private Batting() {
        this(false);
    }
    
    private Batting(boolean derived) {
        this.derived = derived;
    }
    
    public boolean isDerived() {
        return this.derived;
    }
}
