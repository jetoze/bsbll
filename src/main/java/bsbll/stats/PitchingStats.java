package bsbll.stats;

import static tzeth.preconds.MorePreconditions.checkNotNegative;

public final class PitchingStats {
    private final int battersFaced;
    private final int outs;
    private final int hits;
    private final int homeruns;
    private final int walks;
    private final int strikeouts;
    private final int hitByPitches;
    
    public PitchingStats() {
        this(0, 0, 0, 0, 0, 0, 0);
    }
    
    public PitchingStats(int battersFaced,
                         int outs,
                         int hits,
                         int homeruns,
                         int walks,
                         int strikeouts,
                         int hitByPitches) {
        this.battersFaced = checkNotNegative(battersFaced);
        this.outs = checkNotNegative(outs);
        this.hits = checkNotNegative(hits);
        this.homeruns = checkNotNegative(homeruns);
        this.walks = checkNotNegative(walks);
        this.strikeouts = checkNotNegative(strikeouts);
        this.hitByPitches = checkNotNegative(hitByPitches);
    }

    public int getBattersFaced() {
        return battersFaced;
    }
    
    public InningsPitched getInningsPitched() {
        return InningsPitched.fromOuts(outs);
    }
    
    public int getAtBats() {
        return -1;
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getHomeruns() {
        return homeruns;
    }
    
    public int getWalks() {
        return walks;
    }
    
    public int getStrikeouts() {
        return strikeouts;
    }
    
    public int getHitByPitches() {
        return hitByPitches;
    }
    
    public int getRuns() {
        return -1;
    }
    
    public int getEarnedRuns() {
        return -1;
    }
    
    public PitchingStats add(PitchingStats o) {
        return new PitchingStats(
                this.battersFaced + o.battersFaced,
                this.outs + o.outs,
                this.hits + o.hits,
                this.homeruns + o.homeruns,
                this.walks + o.walks,
                this.strikeouts + o.strikeouts,
                this.hitByPitches + o.hitByPitches
        );
    }
    
}
