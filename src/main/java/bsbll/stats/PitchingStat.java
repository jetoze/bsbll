package bsbll.stats;

import bsbll.stats.AbstractStat.AbstractPitchingStat;

public interface PitchingStat<T> extends Stat<T> {
    T get(PitchingStatLine stats);
    
    public static enum PrimitivePitchingStat implements PitchingStat<Integer>, PrimitiveStat {
        GAMES("G"),
        GAMES_STARTED("GS"),
        COMPLETE_GAMES("CG"),
        BATTERS_FACED("BF"),
        OUTS("O"),
        HITS("H"),
        HOMERUNS("HR"),
        STRIKEOUTS("SO"),
        WALKS("BB"),
        EARNED_RUNS("ER"),
        WINS("W"),
        LOSSES("L"),
        SAVES("SV"),
        SHUTOUTS("SHO"),
        HIT_BY_PITCHES("HBP");
        
        private final String abbrev;
        
        private PrimitivePitchingStat(String abbrev) {
            this.abbrev = abbrev;
        }
        
        @Override
        public Integer get(PitchingStatLine stats) {
            return stats.getPrimitiveStat(this);
        }
        
        @Override
        public String abbrev() {
            return this.abbrev;
        }
    }


    public static final PrimitivePitchingStat GAMES = PrimitivePitchingStat.GAMES;
    public static final PrimitivePitchingStat GAMES_STARTED = PrimitivePitchingStat.GAMES_STARTED;
    public static final PrimitivePitchingStat COMPLETE_GAMES = PrimitivePitchingStat.COMPLETE_GAMES;
    public static final PrimitivePitchingStat BATTERS_FACED = PrimitivePitchingStat.BATTERS_FACED;
    public static final PrimitivePitchingStat OUTS = PrimitivePitchingStat.OUTS;
    public static final PrimitivePitchingStat HITS = PrimitivePitchingStat.HITS;
    public static final PrimitivePitchingStat HOMERUNS = PrimitivePitchingStat.HOMERUNS;
    public static final PrimitivePitchingStat STRIKEOUTS = PrimitivePitchingStat.STRIKEOUTS;
    public static final PrimitivePitchingStat WALKS = PrimitivePitchingStat.WALKS;
    public static final PrimitivePitchingStat EARNED_RUNS = PrimitivePitchingStat.EARNED_RUNS;
    public static final PrimitivePitchingStat WINS = PrimitivePitchingStat.WINS;
    public static final PrimitivePitchingStat LOSSES = PrimitivePitchingStat.LOSSES;
    public static final PrimitivePitchingStat SAVES = PrimitivePitchingStat.SAVES;
    public static final PrimitivePitchingStat SHUTOUTS = PrimitivePitchingStat.SHUTOUTS;
    public static final PrimitivePitchingStat HIT_BY_PITCHES = PrimitivePitchingStat.HIT_BY_PITCHES;
    
    public static final PitchingStat<InningsPitched> INNINGS_PITCHED = new AbstractPitchingStat<InningsPitched>("IP") {
        @Override
        public InningsPitched get(PitchingStatLine stats) {
            return InningsPitched.fromOuts(stats.get(OUTS));
        }
    };
    
    public static final PitchingStat<ERA> ERA = new AbstractPitchingStat<ERA>("ERA") {
        @Override
        public bsbll.stats.ERA get(PitchingStatLine stats) {
            return new ERA(stats.get(EARNED_RUNS), stats.get(OUTS));
        }
    };
    
    // TODO: Add more here, like SO/9, BB/9, H/9, WHIP, Opponent BA.

}
