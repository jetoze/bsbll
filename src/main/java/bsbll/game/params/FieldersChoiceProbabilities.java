package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import bsbll.bases.Base;
import bsbll.card.Probability;
import bsbll.die.DieFactory;

/**
 * Provides the probabilities that a batting out event with a given base
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
     */
    private static final Probability DEFAULT = Probability.of(100, 2);
    
    private final ImmutableMap<ImmutableSet<Base>, Probability> probabilities;
    
    public FieldersChoiceProbabilities(ImmutableMap<ImmutableSet<Base>, Probability> probabilities) {
        this.probabilities = requireNonNull(probabilities);
    }

    /**
     * Tests an out with the given situation, to determine if it should be
     * converted to a fielder's choice.
     * 
     * @param situation
     *            the bases that are currently occupied
     * @param dieFactory
     *            the DieFactory that will be asked to produce the die to use in
     *            the test.
     * @return {@code true} if the out should be converted to a fielder's
     *         choice, {@code false} if it should remain as a batter out event.
     */
    public boolean test(Set<Base> situation, DieFactory dieFactory) {
        if (situation.isEmpty()) {
            return false;
        }
        Probability p = probabilities.getOrDefault(situation, DEFAULT);
        return p.test(dieFactory);
    }
    
    @Override
    public String toString() {
        return probabilities.toString();
    }
}
