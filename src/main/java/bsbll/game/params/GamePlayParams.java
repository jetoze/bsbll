package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import bsbll.bases.Advances;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.die.DieFactory;
import bsbll.game.play.EventType;

public final class GamePlayParams {
    private static final GamePlayParams DEFAULT_PARAMS = new GamePlayParams(
            BaseHitAdvanceDistribution.defaultAdvances(),
            OutAdvanceDistribution.defaultAdvances(),
            FieldersChoiceProbabilities.defaultValues(),
            ErrorCountDistribution.noErrors(),
            ErrorAdvanceDistribution.defaultAdvances(),
            PitchingEventProbabilities.defaultProbabilities());
    
    private final BaseHitAdvanceDistribution baseHitAdvanceDistribution;
    private final OutAdvanceDistribution outAdvanceDistribution;
    private final FieldersChoiceProbabilities fieldersChoiceProbabilities;
    private final ErrorCountDistribution errorCountDistribution;
    private final ErrorAdvanceDistribution errorAdvanceDistribution;
    private final PitchingEventProbabilities pitchingEventProbabilities;
    // TODO: Pass in the DieFactory in the constructor instead? Slight downside is that we have to 
    // pass in the same DieFactory to the GamePlayDriver constructor as well.
    
    public GamePlayParams(BaseHitAdvanceDistribution baseHitAdvanceDistribution,
                          OutAdvanceDistribution outAdvanceDistribution,
                          FieldersChoiceProbabilities fieldersChoiceProbabilities,
                          ErrorCountDistribution errorCountDistribution,
                          ErrorAdvanceDistribution errorAdvanceDistribution,
                          PitchingEventProbabilities pitchingEventProbabilities) {
        this.baseHitAdvanceDistribution = requireNonNull(baseHitAdvanceDistribution);
        this.outAdvanceDistribution = requireNonNull(outAdvanceDistribution);
        this.fieldersChoiceProbabilities = requireNonNull(fieldersChoiceProbabilities);
        this.errorCountDistribution = requireNonNull(errorCountDistribution);
        this.errorAdvanceDistribution = requireNonNull(errorAdvanceDistribution);
        this.pitchingEventProbabilities = requireNonNull(pitchingEventProbabilities);
    }

    public static GamePlayParams defaultParams() {
        return DEFAULT_PARAMS;
    }

    public Advances getAdvancesOnBaseHit(BaseHit baseHit, BaseSituation baseSituation, int numberOfOuts, DieFactory dieFactory) {
        return baseHitAdvanceDistribution.pickOne(baseHit, baseSituation, numberOfOuts, dieFactory);
    }
    
    public Advances getMostCommonAdvancesOnBaseHit(BaseHit baseHit, BaseSituation baseSituation, int numberOfOuts) {
        return baseHitAdvanceDistribution.pickMostCommon(baseHit, baseSituation, numberOfOuts);
    }
    
    public int getNumberOfErrors(EventType eventType, BaseSituation baseSituation, DieFactory dieFactory) {
        return errorCountDistribution.getNumberOfErrors(eventType, baseSituation, dieFactory);
    }
    
    public Advances getAdvancesOnError(ErrorAdvanceKey key, BaseSituation baseSituation, int numberOfOuts, DieFactory dieFactory) {
        return errorAdvanceDistribution.pickOne(key, baseSituation, numberOfOuts, dieFactory);
    }
    
    public boolean testFieldersChoice(BaseSituation baseSituation, DieFactory dieFactory) {
        return fieldersChoiceProbabilities.test(baseSituation, dieFactory);
    }
    
    public Advances getAdvancesOnOut(OutAdvanceKey key, BaseSituation baseSituation, int numberOfOuts, DieFactory dieFactory) {
        return outAdvanceDistribution.pickOne(key, baseSituation, numberOfOuts, dieFactory);
    }
    
    public Advances getMostCommonAdvancesOnOut(OutAdvanceKey key, BaseSituation baseSituation, int numberOfOuts) {
        return outAdvanceDistribution.pickMostCommon(key, baseSituation, numberOfOuts);
    }
    
    public Advances getMostCommonAdvancesOnOut(OutAdvanceKey key, 
                                               BaseSituation baseSituation, 
                                               int numberOfOuts,
                                               Predicate<? super Advances> predicate) {
        return outAdvanceDistribution.pickMostCommon(key, baseSituation, numberOfOuts, predicate);
    }
    
    public OutLocation getOutLocation() {
        // TODO: Get from play-by-play data. For now we use a 65-35 split.
        return Math.random() < 0.65
                ? OutLocation.INFIELD
                : OutLocation.OUTFIELD;
        
    }

    public boolean testWildPitch(DieFactory dieFactory) {
        return pitchingEventProbabilities.testWildPitch(dieFactory);
    }
    
    public boolean testPassedBall(DieFactory dieFactory) {
        return pitchingEventProbabilities.testPassedBall(dieFactory);
    }
    
    public boolean testBalk(DieFactory dieFactory) {
        return pitchingEventProbabilities.testBalk(dieFactory);
    }
}
