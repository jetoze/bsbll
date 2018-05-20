package bsbll.stats;

public enum Stat {
    PLATE_APPEARANCE("PA"),
    AT_BAT("AB"),
    HIT("H"),
    SINGLE("S"),
    DOUBLE("2B"),
    TRIPLE("3B"),
    HOMERUN("HR"),
    RUN("R"),
    RUNS_BATTED_IN("RBI"),
    STRIKEOUT("SO"),
    WALK("BB"),
    HIT_BY_PITCH("HBP"),
    SACRIFICE_FLY("SF"),
    SACRIFICE_HIT("SH"),
    BATTING_AVERAGE("BA"),
    SLUGGING_AVERAGE("SA"),
    ON_BASE_PERCENTAGE("OBP"),
    OPS("OPS"),
    EARNED_RUN("ER");
    
    private Stat(String abbrev) {
        this.abbrev = abbrev;
    }
    
    private final String abbrev;
    
    
    @Override
    public String toString() {
        return abbrev;
    }
}
