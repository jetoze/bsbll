package bsbll.stats;

public interface BattingStat<T> {
    T get(BattingStats2 stats);
    
    // TODO: "BasicBattingStat" is perhaps a better name.
    public static enum CountedBattingStat implements BattingStat<Integer> {
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
        public Integer get(BattingStats2 stats) {
            return stats.getCountedStat(this);
        }
    }

    public static final CountedBattingStat GAMES = CountedBattingStat.GAMES;
    public static final CountedBattingStat PLATE_APPEARANCES = CountedBattingStat.PLATE_APPEARANCES;
    public static final CountedBattingStat HITS = CountedBattingStat.HITS;
    public static final CountedBattingStat DOUBLES = CountedBattingStat.DOUBLES;
    public static final CountedBattingStat TRIPLES = CountedBattingStat.TRIPLES;
    public static final CountedBattingStat HOMERUNS = CountedBattingStat.HOMERUNS;
    public static final CountedBattingStat STRIKEOUTS = CountedBattingStat.STRIKEOUTS;
    public static final CountedBattingStat WALKS = CountedBattingStat.WALKS;
    public static final CountedBattingStat RUNS = CountedBattingStat.RUNS;
    public static final CountedBattingStat RUNS_BATTED_IN = CountedBattingStat.RUNS_BATTED_IN;
    public static final CountedBattingStat HIT_BY_PITCHES = CountedBattingStat.HIT_BY_PITCHES;
    public static final CountedBattingStat SACRIFICE_HITS = CountedBattingStat.SACRIFICE_HITS;
    public static final CountedBattingStat SACRIFICE_FLIES = CountedBattingStat.SACRIFICE_FLIES;

    public static final BattingStat<Integer> AT_BATS = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStats2 stats) {
            return PLATE_APPEARANCES.get(stats) - 
                    WALKS.get(stats) -
                    HIT_BY_PITCHES.get(stats) -
                    SACRIFICE_HITS.get(stats) -
                    SACRIFICE_FLIES.get(stats);
        }
    };
    
    public static final BattingStat<Integer> EXTRA_BASE_HITS = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStats2 stats) {
            return DOUBLES.get(stats) +
                    TRIPLES.get(stats) +
                    HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> SINGLES = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStats2 stats) {
            return HITS.get(stats) - EXTRA_BASE_HITS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> TOTAL_BASES = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStats2 stats) {
            return HITS.get(stats) + 
                    DOUBLES.get(stats) + 
                    2 * TRIPLES.get(stats) + 
                    3 * HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Average> BATTING_AVERAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStats2 stats) {
            return new Average(HITS.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> SLUGGING_PERCENTAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStats2 stats) {
            return new Average(TOTAL_BASES.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> ON_BASE_PERCENTAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStats2 stats) {
            return new Average(HITS.get(stats) + WALKS.get(stats) + HIT_BY_PITCHES.get(stats),
                    PLATE_APPEARANCES.get(stats) - SACRIFICE_HITS.get(stats));
        }
    };
    
    public static final BattingStat<Average> OPS = new BattingStat<Average>() {
        @Override
        public Average get(BattingStats2 stats) {
            return Average.sumOf(SLUGGING_PERCENTAGE.get(stats), ON_BASE_PERCENTAGE.get(stats));
        }
    };
    
}
