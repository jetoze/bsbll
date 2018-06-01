package bsbll.stats;

import bsbll.stats.AbstractStat.AbstractBattingStat;

public interface BattingStat<T> extends Stat<T> {
    T get(BattingStatLine statLine);

    public static enum PrimitiveBattingStat implements BattingStat<Integer>, PrimitiveStat {
        GAMES("G"),
        PLATE_APPEARANCES("PA"),
        HITS("H"),
        DOUBLES("2B"),
        TRIPLES("3B"),
        HOMERUNS("HR"),
        STRIKEOUTS("SO"),
        WALKS("BB"),
        RUNS("R"),
        RUNS_BATTED_IN("RBI"),
        HIT_BY_PITCHES("HBP"),
        SACRIFICE_HITS("SH"),
        SACRIFICE_FLIES("SF");
        
        private final String abbrev;
        
        private PrimitiveBattingStat(String abbrev) {
            this.abbrev = abbrev;
        }

        @Override
        public Integer get(BattingStatLine statLine) {
            return statLine.getPrimitiveStat(this);
        }
        
        @Override
        public String abbrev() {
            return this.abbrev;
        }
    }

    public static final PrimitiveBattingStat GAMES = PrimitiveBattingStat.GAMES;
    public static final PrimitiveBattingStat PLATE_APPEARANCES = PrimitiveBattingStat.PLATE_APPEARANCES;
    public static final PrimitiveBattingStat HITS = PrimitiveBattingStat.HITS;
    public static final PrimitiveBattingStat DOUBLES = PrimitiveBattingStat.DOUBLES;
    public static final PrimitiveBattingStat TRIPLES = PrimitiveBattingStat.TRIPLES;
    public static final PrimitiveBattingStat HOMERUNS = PrimitiveBattingStat.HOMERUNS;
    public static final PrimitiveBattingStat STRIKEOUTS = PrimitiveBattingStat.STRIKEOUTS;
    public static final PrimitiveBattingStat WALKS = PrimitiveBattingStat.WALKS;
    public static final PrimitiveBattingStat RUNS = PrimitiveBattingStat.RUNS;
    public static final PrimitiveBattingStat RUNS_BATTED_IN = PrimitiveBattingStat.RUNS_BATTED_IN;
    public static final PrimitiveBattingStat HIT_BY_PITCHES = PrimitiveBattingStat.HIT_BY_PITCHES;
    public static final PrimitiveBattingStat SACRIFICE_HITS = PrimitiveBattingStat.SACRIFICE_HITS;
    public static final PrimitiveBattingStat SACRIFICE_FLIES = PrimitiveBattingStat.SACRIFICE_FLIES;
    
    public static final BattingStat<Integer> AT_BATS = new AbstractBattingStat<Integer>("AB") {
        @Override
        public Integer get(BattingStatLine stats) {
            return PLATE_APPEARANCES.get(stats) - 
                    WALKS.get(stats) -
                    HIT_BY_PITCHES.get(stats) -
                    SACRIFICE_HITS.get(stats) -
                    SACRIFICE_FLIES.get(stats);
        }
    };
    
    public static final BattingStat<Integer> EXTRA_BASE_HITS = new AbstractBattingStat<Integer>("XBH") {
        @Override
        public Integer get(BattingStatLine stats) {
            return DOUBLES.get(stats) +
                    TRIPLES.get(stats) +
                    HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> SINGLES = new AbstractBattingStat<Integer>("S") {
        @Override
        public Integer get(BattingStatLine stats) {
            return HITS.get(stats) - EXTRA_BASE_HITS.get(stats);
        }
    };
    
    public static final BattingStat<Integer> TOTAL_BASES = new AbstractBattingStat<Integer>("TB") {
        @Override
        public Integer get(BattingStatLine stats) {
            return HITS.get(stats) + 
                    DOUBLES.get(stats) + 
                    2 * TRIPLES.get(stats) + 
                    3 * HOMERUNS.get(stats);
        }
    };
    
    public static final BattingStat<Average> BATTING_AVERAGE = new AbstractBattingStat<Average>("BA") {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(HITS.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> SLUGGING_PERCENTAGE = new AbstractBattingStat<Average>("SLG") {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(TOTAL_BASES.get(stats), AT_BATS.get(stats));
        }
    };
    
    public static final BattingStat<Average> ON_BASE_PERCENTAGE = new AbstractBattingStat<Average>("OBP") {
        @Override
        public Average get(BattingStatLine stats) {
            return new Average(HITS.get(stats) + WALKS.get(stats) + HIT_BY_PITCHES.get(stats),
                    PLATE_APPEARANCES.get(stats) - SACRIFICE_HITS.get(stats));
        }
    };
    
    public static final BattingStat<Average> OPS = new AbstractBattingStat<Average>("OPS") {
        @Override
        public Average get(BattingStatLine stats) {
            return Average.sumOf(SLUGGING_PERCENTAGE.get(stats), ON_BASE_PERCENTAGE.get(stats));
        }
    };
}
