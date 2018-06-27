package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.card.Probability;
import bsbll.player.PlayerId;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.ParsedPlay;

public abstract class PitchingEventProbabilitiesFactory {

    public abstract PitchingEventProbabilities getProbabilities();

    public static PitchingEventProbabilitiesFactory defaultValuse() {
        return DefaultProbabilitiesFactory.INSTANCE;
    }
    
    public static PitchingEventProbabilitiesFactory retrosheet(Year year) {
        return new RetrosheetFactory(requireNonNull(year));
    }
    
    private static class DefaultProbabilitiesFactory extends PitchingEventProbabilitiesFactory {
        private static final DefaultProbabilitiesFactory INSTANCE = new DefaultProbabilitiesFactory();

        @Override
        public PitchingEventProbabilities getProbabilities() {
            return PitchingEventProbabilities.defaultProbabilities();
        }
    }
    
    
    private static class RetrosheetFactory extends PitchingEventProbabilitiesFactory {
        private final Year year;
        
        public RetrosheetFactory(Year year) {
            this.year = year;
        }

        @Override
        public PitchingEventProbabilities getProbabilities() {
            Handler handler = new Handler();
            handler.parseAll(year);
            return handler.getResult();
        }
        
        private class Handler extends DefaultGameHandler {
            private int plateAppearances;
            private int wildPitches;
            private int passedBalls;
            private int balks;
            
            private PlayerId previousBatter;

            @Override
            protected void process(ParsedPlay play, BaseSituation bases, int outs) {
                if (previousBatter == null || !play.getBatterId().equals(previousBatter)) {
                    // This logic is not correct, since a plate appearance can span over
                    // multiple innings (e.g. the last out is made on a caught stealing).
                    if (!bases.areEmpty()) {
                        ++plateAppearances;
                    }
                    previousBatter = play.getBatterId();
                }
                switch (play.getType()) {
                case WILD_PITCH:
                    ++wildPitches;
                    break;
                case PASSED_BALL:
                    ++passedBalls;
                    break;
                case BALK:
                    ++balks;
                    break;
                default:
                    // not of interest
                }
            }
            
            public PitchingEventProbabilities getResult() {
                return new PitchingEventProbabilities(
                        Probability.of(wildPitches, plateAppearances), 
                        Probability.of(passedBalls, plateAppearances),
                        Probability.of(balks, plateAppearances));
            }
        }
    }
    
    
    public static void main(String[] args) {
        System.out.println(PitchingEventProbabilitiesFactory.retrosheet(Year.of(1925)).getProbabilities());
    }
}
