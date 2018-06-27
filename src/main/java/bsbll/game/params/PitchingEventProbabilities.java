package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.card.Probability;
import bsbll.die.DieFactory;

public final class PitchingEventProbabilities {
    private static final PitchingEventProbabilities ZERO = new PitchingEventProbabilities(
            Probability.ZERO, Probability.ZERO, Probability.ZERO);
    
    private static final PitchingEventProbabilities DEFAULT = new PitchingEventProbabilities(
            Probability.of(0.008), Probability.of(0.003), Probability.of(0.001));
    
    // TODO: Obvoiusly not very realistic, since some pitchers presumably are more
    // prone to wild pitches than others.
    private final Probability wildPitchProbability;
    private final Probability passedBallProbability;
    private final Probability balkProbability;

    public PitchingEventProbabilities(Probability wildPitchProbability,
                                      Probability passedBallProbability, 
                                      Probability balkProbability) {
        this.wildPitchProbability = requireNonNull(wildPitchProbability);
        this.passedBallProbability = requireNonNull(passedBallProbability);
        this.balkProbability = requireNonNull(balkProbability);
    }
    
    public static PitchingEventProbabilities zeroProbabilities() {
        return ZERO;
    }
    
    public static PitchingEventProbabilities defaultProbabilities() {
        return DEFAULT;
    }

    public boolean testWildPitch(DieFactory dieFactory) {
        return wildPitchProbability.test(dieFactory);
    }
    
    public boolean testPassedBall(DieFactory dieFactory) {
        return passedBallProbability.test(dieFactory);
    }
    
    public boolean testBalk(DieFactory dieFactory) {
        return balkProbability.test(dieFactory);
    }

    @Override
    public String toString() {
        return String.format("Wild Pitch: %s. Passed Ball: %s. Balk: %s", 
                wildPitchProbability, passedBallProbability, balkProbability);
    }
}
