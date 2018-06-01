package bsbll.stats;

public interface BattingStat<T> extends Stat<T> {
    T get(BattingStatLine statLine);

    public static enum BasicBattingStat implements BattingStat<Integer>, BasicStat<BasicBattingStat> {
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
        public BasicBattingStat self() {
            return this;
        }

        @Override
        public Integer get(BattingStatLine statLine) {
            return statLine.getBasicStat(this);
        }
    }

    public static final BasicBattingStat GAMES = BasicBattingStat.GAMES;
    public static final BasicBattingStat PLATE_APPEARANCES = BasicBattingStat.PLATE_APPEARANCES;
    public static final BasicBattingStat HITS = BasicBattingStat.HITS;
    public static final BasicBattingStat DOUBLES = BasicBattingStat.DOUBLES;
    public static final BasicBattingStat TRIPLES = BasicBattingStat.TRIPLES;
    public static final BasicBattingStat HOMERUNS = BasicBattingStat.HOMERUNS;
    public static final BasicBattingStat STRIKEOUTS = BasicBattingStat.STRIKEOUTS;
    public static final BasicBattingStat WALKS = BasicBattingStat.WALKS;
    public static final BasicBattingStat RUNS = BasicBattingStat.RUNS;
    public static final BasicBattingStat RUNS_BATTED_IN = BasicBattingStat.RUNS_BATTED_IN;
    public static final BasicBattingStat HIT_BY_PITCHES = BasicBattingStat.HIT_BY_PITCHES;
    public static final BasicBattingStat SACRIFICE_HITS = BasicBattingStat.SACRIFICE_HITS;
    public static final BasicBattingStat SACRIFICE_FLIES = BasicBattingStat.SACRIFICE_FLIES;

    public static final BattingStat<Integer> AT_BATS = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStatLine stats) {
            return PLATE_APPEARANCES.get(stats) - 
                    WALKS.get(stats) -
                    HIT_BY_PITCHES.get(stats) -
                    SACRIFICE_HITS.get(stats) -
                    SACRIFICE_FLIES.get(stats);
        }
    };
    
    public static final BattingStat<Integer> EXTRA_BASE_HITS = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStatLine stats) {
            return DOUBLES.get(stats) +
                    TRIPLES.get(stats) +
                    HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> SINGLES = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStatLine stats) {
            return HITS.get(stats) - EXTRA_BASE_HITS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> TOTAL_BASES = new BattingStat<Integer>() {
        @Override
        public Integer get(BattingStatLine stats) {
            return HITS.get(stats) + 
                    DOUBLES.get(stats) + 
                    2 * TRIPLES.get(stats) + 
                    3 * HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Average> BATTING_AVERAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(HITS.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> SLUGGING_PERCENTAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(TOTAL_BASES.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> ON_BASE_PERCENTAGE = new BattingStat<Average>() {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(HITS.get(stats) + WALKS.get(stats) + HIT_BY_PITCHES.get(stats),
                    PLATE_APPEARANCES.get(stats) - SACRIFICE_HITS.get(stats));
        }
    };
    
    public static final BattingStat<Average> OPS = new BattingStat<Average>() {
        @Override
        public Average get(BattingStatLine stats) {
            return Average.sumOf(SLUGGING_PERCENTAGE.get(stats), ON_BASE_PERCENTAGE.get(stats));
        }
    };

}
