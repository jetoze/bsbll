package bsbll.stats;

import bsbll.matchup.Matchup.Outcome;

public final class BattingStats {
    private final int plateAppearances;
    private final int hits;
    private final int doubles;
    private final int triples;
    private final int homeruns;
    private final int walks;
    private final int strikeouts;
    private final int hitByPitches;
    
    // TODO: More stats here, such as R, RBI, SF, SH. Come up with a better
    // data representation of it?
    
    public BattingStats() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }
    
    public BattingStats(int plateAppearances, int hits, int doubles, int triples, int homeruns,
            int walks, int strikeouts, int hitByPitches) {
        // TODO: Preconditions. No negatives. The sum of (hits + walks + strikeouts + hitByPitches)
        // must be <= plateAppearances.
        this.plateAppearances = plateAppearances;
        this.hits = hits;
        this.doubles = doubles;
        this.triples = triples;
        this.homeruns = homeruns;
        this.walks = walks;
        this.strikeouts = strikeouts;
        this.hitByPitches = hitByPitches;
    }

    public int getPlateAppearances() {
        return plateAppearances;
    }

    public int getAtBats() {
        return plateAppearances - walks - hitByPitches;
    }
    
    public Average getBattingAverage() {
        return new Average(hits, getAtBats());
    }
    
    public Average getSluggingPercentage() {
        return new Average(getTotalBases(), getAtBats());
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getSingles() {
        return hits - getExtraBaseHits();
    }

    public int getDoubles() {
        return doubles;
    }

    public int getTriples() {
        return triples;
    }

    public int getHomeruns() {
        return homeruns;
    }
    
    public int getExtraBaseHits() {
        return doubles + triples + homeruns;
    }
    
    public int getTotalBases() {
        return hits + doubles + 2 * triples + 3 * homeruns;
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
    
    public BattingStats add(Outcome outcome) {
        int pa = this.plateAppearances + 1;
        int h = this.hits + (outcome.isHit() ? 1 : 0);
        int d = this.doubles + (outcome == Outcome.DOUBLE ? 1 : 0);
        int t = this.triples + (outcome == Outcome.TRIPLE ? 1 : 0);
        int hr = this.homeruns + (outcome == Outcome.HOMERUN ? 1 : 0);
        int w = this.walks + (outcome == Outcome.WALK ? 1 : 0);
        int so = this.strikeouts + (outcome == Outcome.STRIKEOUT ? 1 : 0);
        int hbp = this.hitByPitches + (outcome == Outcome.HIT_BY_PITCH ? 1 : 0);
        return new BattingStats(pa, h, d, t, hr, w, so, hbp);
    }
    
    public BattingStats add(BattingStats o) {
        return new BattingStats(
                this.plateAppearances + o.plateAppearances,
                this.hits + o.hits,
                this.doubles + o.doubles,
                this.triples + o.triples,
                this.homeruns + o.homeruns,
                this.walks + o.walks,
                this.strikeouts + o.strikeouts,
                this.hitByPitches + o.hitByPitches
        );
    }

}
