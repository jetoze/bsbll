package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.card.Probability;
import bsbll.die.DieFactory;
import p3.Persister;

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
    
    public void store(Persister p) {
        Storage.store(this, p);
    }
    
    public static PitchingEventProbabilities restoreFrom(Persister p) {
        return Storage.restoreFrom(p);
    }
    
    private static class Storage {
        private static final String WILD_PITCH = "WP";
        private static final String PASSED_BALL = "PB";
        private static final String BALK = "BK";
        
        public static void store(PitchingEventProbabilities peb, Persister p) {
            p.putDouble(WILD_PITCH, peb.wildPitchProbability.asDouble());
            p.putDouble(PASSED_BALL, peb.passedBallProbability.asDouble());
            p.putDouble(BALK, peb.balkProbability.asDouble());
        }
        
        public static PitchingEventProbabilities restoreFrom(Persister p) {
            return new PitchingEventProbabilities(
                    Probability.of(p.getDouble(WILD_PITCH)),
                    Probability.of(p.getDouble(PASSED_BALL)),
                    Probability.of(p.getDouble(BALK)));
        }
    }
}
