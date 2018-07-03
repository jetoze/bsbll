package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.Year;

public abstract class GamePlayParamsFactory {

    public abstract GamePlayParams createParams();

    public static GamePlayParamsFactory defaultParams() {
        return DefaultParamsFactory.INSTANCE;
    }
    
    public static GamePlayParamsFactory retrosheet(Year year) {
        return new RetrosheetFactory(requireNonNull(year));
    }
    
    
    private static class DefaultParamsFactory extends GamePlayParamsFactory {
        private static final DefaultParamsFactory INSTANCE = new DefaultParamsFactory();

        @Override
        public GamePlayParams createParams() {
            return GamePlayParams.defaultParams();
        }
    }
    
    
    private static class RetrosheetFactory extends GamePlayParamsFactory {
        private final Year year;

        public RetrosheetFactory(Year year) {
            this.year = year;
        }
        
        @Override
        public GamePlayParams createParams() {
            BaseHitAdvanceDistribution baseHitAdvanceDistribution = BaseHitAdvanceDistributionFactory
                    .retrosheet(year)
                    .createDistribution();
            OutAdvanceDistribution outAdvanceDistribution = OutAdvanceDistributionFactory
                    .retrosheet(year)
                    .createDistribution();
            FieldersChoiceProbabilities fieldersChoiceProbabilities = FieldersChoiceProbabilitiesFactory
                    .retrosheet(year)
                    .createProbabilities();
            ErrorCountDistribution errorCountDistribution = ErrorCountDistributionFactory.retrosheet(year)
                    .createDistribution();
            ErrorAdvanceDistribution errorAdvanceDistribution = ErrorAdvanceDistributionFactory.retrosheet(year)
                    .createDistribution();
            PitchingEventProbabilities pitchingEventProbabilities = PitchingEventProbabilitiesFactory.retrosheet(year)
                    .getProbabilities();
            return new GamePlayParams(
                    baseHitAdvanceDistribution,
                    outAdvanceDistribution,
                    fieldersChoiceProbabilities,
                    errorCountDistribution,
                    errorAdvanceDistribution,
                    pitchingEventProbabilities);
        }
    }
}
