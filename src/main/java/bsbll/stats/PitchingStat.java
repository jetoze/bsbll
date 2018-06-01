package bsbll.stats;

public interface PitchingStat<T> extends Stat<T> {
    T get(PitchingStatLine stats);
    
    public static enum PrimitivePitchingStat implements PitchingStat<Integer>, PrimitiveStat<PrimitivePitchingStat> {
        GAMES,
        GAMES_STARTED,
        COMPLETE_GAMES,
        BATTERS_FACED,
        OUTS,
        HITS,
        HOMERUNS,
        STRIKEOUTS,
        WALKS,
        EARNED_RUNS,
        WINS,
        LOSSES,
        SAVES,
        SHUTOUTS,
        HIT_BY_PITCHES;
        
        @Override
        public Integer get(PitchingStatLine stats) {
            return stats.getPrimitiveStat(this);
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
    
    public static final PitchingStat<InningsPitched> INNINGS_PITCHED = new PitchingStat<InningsPitched>() {
        @Override
        public InningsPitched get(PitchingStatLine stats) {
            return InningsPitched.fromOuts(stats.get(OUTS));
        }
    };
    
    public static final PitchingStat<ERA> ERA = new PitchingStat<ERA>() {
        @Override
        public bsbll.stats.ERA get(PitchingStatLine stats) {
            return new ERA(stats.get(EARNED_RUNS), stats.get(OUTS));
        }
    };
    
    // TODO: Add more here, like SO/9, BB/9, H/9, WHIP, Opponent BA.

}
