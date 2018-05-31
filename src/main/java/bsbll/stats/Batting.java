package bsbll.stats;

public interface Batting<T> {
    T get(BattingStats stats);
    
    // TODO: "BasicBattingStat" is perhaps a better name.
    public static enum BasicBatting implements Batting<Integer> {
        GAMES,
        PLATE_APPEARANCES,
        HITS,
        DOUBLES,
        TRIPLES,
        HOMERUNS,
        STRIKEOUTS,
        WALKS,
        RUNS,
        RUNS_BATTED_IN,
        HIT_BY_PITCHES,
        SACRIFICE_HITS,
        SACRIFICE_FLIES;

        @Override
        public Integer get(BattingStats stats) {
            return stats.getBasicStat(this);
        }
    }

    public static final BasicBatting GAMES = BasicBatting.GAMES;
    public static final BasicBatting PLATE_APPEARANCES = BasicBatting.PLATE_APPEARANCES;
    public static final BasicBatting HITS = BasicBatting.HITS;
    public static final BasicBatting DOUBLES = BasicBatting.DOUBLES;
    public static final BasicBatting TRIPLES = BasicBatting.TRIPLES;
    public static final BasicBatting HOMERUNS = BasicBatting.HOMERUNS;
    public static final BasicBatting STRIKEOUTS = BasicBatting.STRIKEOUTS;
    public static final BasicBatting WALKS = BasicBatting.WALKS;
    public static final BasicBatting RUNS = BasicBatting.RUNS;
    public static final BasicBatting RUNS_BATTED_IN = BasicBatting.RUNS_BATTED_IN;
    public static final BasicBatting HIT_BY_PITCHES = BasicBatting.HIT_BY_PITCHES;
    public static final BasicBatting SACRIFICE_HITS = BasicBatting.SACRIFICE_HITS;
    public static final BasicBatting SACRIFICE_FLIES = BasicBatting.SACRIFICE_FLIES;

    public static final Batting<Integer> AT_BATS = new Batting<Integer>() {
        @Override
        public Integer get(BattingStats stats) {
            return PLATE_APPEARANCES.get(stats) - 
                    WALKS.get(stats) -
                    HIT_BY_PITCHES.get(stats) -
                    SACRIFICE_HITS.get(stats) -
                    SACRIFICE_FLIES.get(stats);
        }
    };
    
    public static final Batting<Integer> EXTRA_BASE_HITS = new Batting<Integer>() {
        @Override
        public Integer get(BattingStats stats) {
            return DOUBLES.get(stats) +
                    TRIPLES.get(stats) +
                    HOMERUNS.get(stats);
        }
    };
    
    public static final Batting<Integer> SINGLES = new Batting<Integer>() {
        @Override
        public Integer get(BattingStats stats) {
            return HITS.get(stats) - EXTRA_BASE_HITS.get(stats);
        }
    };
    
    public static final Batting<Integer> TOTAL_BASES = new Batting<Integer>() {
        @Override
        public Integer get(BattingStats stats) {
            return HITS.get(stats) + 
                    DOUBLES.get(stats) + 
                    2 * TRIPLES.get(stats) + 
                    3 * HOMERUNS.get(stats);
        }
    };
    
    public static final Batting<Average> BATTING_AVERAGE = new Batting<Average>() {
        @Override
        public Average get(BattingStats stats) {
            return new Average(HITS.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final Batting<Average> SLUGGING_PERCENTAGE = new Batting<Average>() {
        @Override
        public Average get(BattingStats stats) {
            return new Average(TOTAL_BASES.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final Batting<Average> ON_BASE_PERCENTAGE = new Batting<Average>() {
        @Override
        public Average get(BattingStats stats) {
            return new Average(HITS.get(stats) + WALKS.get(stats) + HIT_BY_PITCHES.get(stats),
                    PLATE_APPEARANCES.get(stats) - SACRIFICE_HITS.get(stats));
        }
    };
    
    public static final Batting<Average> OPS = new Batting<Average>() {
        @Override
        public Average get(BattingStats stats) {
            return Average.sumOf(SLUGGING_PERCENTAGE.get(stats), ON_BASE_PERCENTAGE.get(stats));
        }
    };
    
}
