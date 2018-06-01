package bsbll.stats;

public interface PitchingStat<T> extends Stat<T> {
    T get(PitchingStatLine stats);
    
    public static enum BasicPitchingStat implements PitchingStat<Integer>, BasicStat<BasicPitchingStat> {
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
            return stats.getBasicStat(this);
        }

        @Override
        public BasicPitchingStat self() {
            return this;
        }
    }


    public static final BasicPitchingStat GAMES = BasicPitchingStat.GAMES;
    public static final BasicPitchingStat GAMES_STARTED = BasicPitchingStat.GAMES_STARTED;
    public static final BasicPitchingStat COMPLETE_GAMES = BasicPitchingStat.COMPLETE_GAMES;
    public static final BasicPitchingStat BATTERS_FACED = BasicPitchingStat.BATTERS_FACED;
    public static final BasicPitchingStat OUTS = BasicPitchingStat.OUTS;
    public static final BasicPitchingStat HITS = BasicPitchingStat.HITS;
    public static final BasicPitchingStat HOMERUNS = BasicPitchingStat.HOMERUNS;
    public static final BasicPitchingStat STRIKEOUTS = BasicPitchingStat.STRIKEOUTS;
    public static final BasicPitchingStat WALKS = BasicPitchingStat.WALKS;
    public static final BasicPitchingStat EARNED_RUNS = BasicPitchingStat.EARNED_RUNS;
    public static final BasicPitchingStat WINS = BasicPitchingStat.WINS;
    public static final BasicPitchingStat LOSSES = BasicPitchingStat.LOSSES;
    public static final BasicPitchingStat SAVES = BasicPitchingStat.SAVES;
    public static final BasicPitchingStat SHUTOUTS = BasicPitchingStat.SHUTOUTS;
    public static final BasicPitchingStat HIT_BY_PITCHES = BasicPitchingStat.HIT_BY_PITCHES;
    
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
