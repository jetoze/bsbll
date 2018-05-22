package bsbll.card;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

import bsbll.stats.BattingStats;
import bsbll.stats.PitchingStats;

public final class PlayerCard {
    private final Probability hits;
    private final Probability doubles;
    private final Probability triples;
    private final Probability homeruns;
    private final Probability strikeouts;
    private final Probability walks;
    private final Probability hitByPitches;
    
    public PlayerCard(Probability hits,
                      Probability doubles,
                      Probability triples,
                      Probability homeruns,
                      Probability strikeouts,
                      Probability walks,
                      Probability hitByPitches) {
        this.hits = requireNonNull(hits);
        this.doubles = requireNonNull(doubles);
        this.triples = requireNonNull(triples);
        this.homeruns = requireNonNull(homeruns);
        this.strikeouts = requireNonNull(strikeouts);
        this.walks = requireNonNull(walks);
        this.hitByPitches = requireNonNull(hitByPitches);
        // TODO: Preconditions, or make the ctor private.
    }

    // TODO: What is a good naming strategy for the getters? I would
    //       prefer something simple like strikeout(), homerun(), etc,
    //       but that breaks down for double().

    public Probability hits() {
        return hits;
    }
    
    public Probability singles() {
        return hits.subtract(doubles).subtract(triples).subtract(homeruns);
    }
    
    public Probability doubles() {
        return doubles;
    }
    
    public Probability triples() {
        return triples;
    }
    
    public Probability homeruns() {
        return homeruns;
    }
    
    public Probability extraBaseHits() {
        return doubles.add(triples).add(homeruns);
    }
    
    public Probability strikeouts() {
        return strikeouts;
    }
    
    public Probability walks() {
        return walks;
    }
    
    public Probability hitByPitches() {
        return hitByPitches;
    }
    
    /**
     * The combined probability of hit and batted out.
     */
    public Probability contact() {
        return Probability.complementOf(noContact());
    }
    
    /**
     * The combined probability of strikeout, walk, and hit by pitch.
     */
    public Probability noContact() {
        return strikeouts.add(walks).add(hitByPitches);
    }
    
    public Probability battedOuts() {
        return contact().subtract(hits);
    }
    
    public static Builder builder(int plateAppearances) {
        return new Builder(plateAppearances);
    }
    
    public static PlayerCard of(BattingStats stats) {
        return builder(stats.getPlateAppearances())
                .hits(stats.getHits())
                .doubles(stats.getDoubles())
                .triples(stats.getTriples())
                .homeruns(stats.getHomeruns())
                .walks(stats.getWalks())
                .strikeouts(stats.getStrikeouts())
                .hitByPitches(stats.getHitByPitches())
                .build();
    }
    
    /**
     * Creates a PlayerCard from pitching stats.
     * 
     * @param stats
     *            the pitching stats
     * @param league
     *            the overall PlayerCard for the league. This is used to fill in
     *            unknowns such as doubles and triples, that are not covered by
     *            PitchingStats.
     */
    public static PlayerCard of(PitchingStats stats, PlayerCard league) {
        return builder(stats.getBattersFaced())
                .hits(stats.getHits())
                .doubles(league.doubles())
                .triples(league.triples())
                .homeruns(stats.getHomeruns())
                .walks(stats.getWalks())
                .strikeouts(stats.getStrikeouts())
                .hitByPitches(stats.getHitByPitches())
                .build();
    }
    
    
    public static final class Builder {
        private final int plateAppearances;
        private int strikeouts;
        private int walks;
        private int hits;
        private int doubles;
        private int triples;
        private int homeruns;
        private int hitByPitches;
        
        public Builder(int plateAppearances) {
            this.plateAppearances = checkPositive(plateAppearances);
        }
        
        public Builder hits(int n) {
            this.hits = checkNotNegative(n);
            return this;
        }
        
        public Builder doubles(int n) {
            this.doubles = checkNotNegative(n);
            return this;
        }
        
        public Builder doubles(Probability p) {
            this.doubles = p.apply(plateAppearances);
            return this;
        }
        
        public Builder triples(int n) {
            this.triples = checkNotNegative(n);
            return this;
        }
        
        public Builder triples(Probability p) {
            this.triples = p.apply(plateAppearances);
            return this;
        }
        
        public Builder homeruns(int n) {
            this.homeruns = checkNotNegative(n);
            return this;
        }
        
        public Builder strikeouts(int n) {
            this.strikeouts = checkNotNegative(n);
            return this;
        }

        public Builder walks(int n) {
            this.walks = checkNotNegative(n);
            return this;
        }
        
        public Builder hitByPitches(int n) {
            this.hitByPitches = checkNotNegative(n);
            return this;
        }
        
        public PlayerCard build() {
            int total = hits + strikeouts + walks + hitByPitches;
            checkArgument(total <= plateAppearances, 
                    "Too many events, %s, for the number of plate appearances, %s", total, plateAppearances);
            int xBaseHits = doubles + triples + homeruns;
            checkArgument(xBaseHits <= hits, 
                    "Too many extra base hits, %s, for the number of hits, %s", xBaseHits, hits);
            return new PlayerCard(
                    Probability.of(hits, plateAppearances), 
                    Probability.of(doubles, plateAppearances), 
                    Probability.of(triples, plateAppearances), 
                    Probability.of(homeruns, plateAppearances),
                    Probability.of(strikeouts, plateAppearances),
                    Probability.of(walks, plateAppearances),
                    Probability.of(hitByPitches, plateAppearances));
        }
    }
    
}
