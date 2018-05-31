package bsbll.stats;

public interface Pitching<T> {
    T get(PitchingStats stats);
    
    public static enum BasicPitching implements Pitching<Integer> {
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
        public Integer get(PitchingStats stats) {
            return stats.getBasicStat(this);
        }
    }
    
    public static final BasicPitching GAMES = BasicPitching.GAMES;
    public static final BasicPitching GAMES_STARTED = BasicPitching.GAMES_STARTED;
    public static final BasicPitching COMPLETE_GAMES = BasicPitching.COMPLETE_GAMES;
    public static final BasicPitching BATTERS_FACED = BasicPitching.BATTERS_FACED;
    public static final BasicPitching OUTS = BasicPitching.OUTS;
    public static final BasicPitching HITS = BasicPitching.HITS;
    public static final BasicPitching HOMERUNS = BasicPitching.HOMERUNS;
    public static final BasicPitching STRIKEOUTS = BasicPitching.STRIKEOUTS;
    public static final BasicPitching WALKS = BasicPitching.WALKS;
    public static final BasicPitching EARNED_RUNS = BasicPitching.EARNED_RUNS;
    public static final BasicPitching WINS = BasicPitching.WINS;
    public static final BasicPitching LOSSES = BasicPitching.LOSSES;
    public static final BasicPitching SAVES = BasicPitching.SAVES;
    public static final BasicPitching SHUTOUTS = BasicPitching.SHUTOUTS;
    public static final BasicPitching HIT_BY_PITCHES = BasicPitching.HIT_BY_PITCHES;
    
    public static final Pitching<InningsPitched> INNINGS_PITCHED = new Pitching<InningsPitched>() {
        @Override
        public InningsPitched get(PitchingStats stats) {
            return InningsPitched.fromOuts(stats.get(OUTS));
        }
    };
    
    public static final Pitching<ERA> ERA = new Pitching<ERA>() {
        @Override
        public bsbll.stats.ERA get(PitchingStats stats) {
            return new ERA(stats.get(EARNED_RUNS), stats.get(OUTS));
        }
    };
    
    // TODO: Add more here, like SO/9, BB/9, H/9, WHIP, Opponent BA.
}
