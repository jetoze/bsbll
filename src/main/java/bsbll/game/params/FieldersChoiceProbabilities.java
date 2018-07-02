package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import bsbll.card.Probability;
import bsbll.die.DieFactory;
import p3.Persister;

/**
 * Provides the probabilities that an infield out event with a given base
 * situation results in a fielder's choice,
 * <p>
 * The current implementation is static in the sense that it doesn't take into
 * account the game context, or the individual speed of the base runners. In
 * reality, for example, the fielding team would be more interested in throwing
 * home, trying to get the lead runner, in the bottom of the ninth inning if the
 * game is tied, compared to if they are up by 10 runs.
 */
@Immutable
public final class FieldersChoiceProbabilities {
    /**
     * The default probability if the real number is not known.
     * <p>
     * TODO: More realistic would be to use one default probability for each base situation.
     */
    private static final Probability DEFAULT_PROBABILITY = Probability.of(2, 100);
    
    private static final FieldersChoiceProbabilities DEFAULT = new FieldersChoiceProbabilities(ImmutableMap.of());
    
    private final ImmutableMap<OccupiedBases, Probability> probabilities;

    /**
     * Creates a FieldersChoiceProbabilities instance based on the given
     * probabilities. Any base situation not included in the provided data will
     * be treated using a {@link #defaultValues() default} probability.
     */
    public FieldersChoiceProbabilities(ImmutableMap<OccupiedBases, Probability> probabilities) {
        this.probabilities = requireNonNull(probabilities);
    }
    
    /**
     * Returns a FieldersChoiceProbabilities instance that uses a static,
     * default value for all decisions.
     */
    public static FieldersChoiceProbabilities defaultValues() {
        return DEFAULT;
    }

    /**
     * Tests an out with the given situation to determine if it should be
     * converted to a fielder's choice, using the internal (random) DieFactory.
     * 
     * @param situation
     *            the current base situation
     * @return {@code true} if the out should be converted to a fielder's
     *         choice, {@code false} if it should remain as a batter out event. Always returns
     *         {@code true} if the bases are empty.
     */
    public boolean test(BaseSituation situation, DieFactory dieFactory) {
        if (situation.areEmpty()) {
            return false;
        }
        Probability p = probabilities.getOrDefault(situation.getOccupiedBases(), DEFAULT_PROBABILITY);
        return p.test(dieFactory);
    }
    
    @Override
    public String toString() {
        return probabilities.toString();
    }
    
    public void store(Persister p) {
        for (Map.Entry<OccupiedBases, Probability> e : probabilities.entrySet()) {
            p.newChild("Entry")
                .putString("Bases", e.getKey().name())
                .putDouble("Value", e.getValue().asDouble());
        }
    }
    
    public static FieldersChoiceProbabilities restoreFrom(Persister p) {
        ImmutableMap.Builder<OccupiedBases, Probability> builder = ImmutableMap.builder();
        for (Persister c : p.getChildren("Entry")) {
            builder.put(OccupiedBases.valueOf(c.getString("Bases")), Probability.of(p.getDouble("Value")));
        }
        return new FieldersChoiceProbabilities(builder.build());
    }
    
    public static Builder builder() {
        return new Builder();
    }

    
    public static final class Builder {
        private final ImmutableMap.Builder<OccupiedBases, Probability> data = ImmutableMap.builder();
        
        public Builder add(OccupiedBases bases, int outs, int fcs) {
            data.put(bases, Probability.of(fcs, fcs + outs));
            return this;
        }
        
        public FieldersChoiceProbabilities build() {
            return new FieldersChoiceProbabilities(data.build());
        }
    }
}
