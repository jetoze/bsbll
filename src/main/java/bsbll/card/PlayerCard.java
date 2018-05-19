package bsbll.card;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

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
    }

    // TODO: What is a good naming strategy for the getters? I would
    //       prefer something simple like strikeout(), homerun(), etc,
    //       but that breaks down for double().

    public Probability hits() {
        return hits;
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
    
    public Probability strikeouts() {
        return strikeouts;
    }
    
    public Probability walks() {
        return walks;
    }
    
    public Probability hitByPitches() {
        return hitByPitches;
    }
    
    public static Builder builder(int plateAppearances) {
        return new Builder(plateAppearances);
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
        
        public Builder triples(int n) {
            this.triples = checkNotNegative(n);
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
            int total = hits + doubles + triples + homeruns + strikeouts +
                    walks + hitByPitches;
            checkArgument(total <= plateAppearances, 
                    "Too many events, %s, for the number of plate appearances, %s", total, plateAppearances);
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
