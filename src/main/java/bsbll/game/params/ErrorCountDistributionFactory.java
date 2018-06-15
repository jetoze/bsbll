package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.DefaultGameHandler;

public abstract class ErrorCountDistributionFactory {

    public abstract ErrorCountDistribution createDistribution();

    public static ErrorCountDistributionFactory retrosheet(Year year) {
        requireNonNull(year);
        return new RetrosheetFactory(year);
    }
    
    public static ErrorCountDistributionFactory noErrors() {
        return NoErrorsFactory.INSTANCE;
    }
    
    
    private static final class NoErrorsFactory extends ErrorCountDistributionFactory {
        private static final NoErrorsFactory INSTANCE = new NoErrorsFactory();

        @Override
        public ErrorCountDistribution createDistribution() {
            return ErrorCountDistribution.noErrors();
        }
    }
    
    
    private static final class RetrosheetFactory extends ErrorCountDistributionFactory {
        private final Year year;
        
        public RetrosheetFactory(Year year) {
            this.year = year;
        }
        
        @Override
        public ErrorCountDistribution createDistribution() {
            Handler handler = new Handler();
            handler.parseAll(year);
            return handler.getResult();
        }
        
        private class Handler extends DefaultGameHandler {
            private final ErrorCountDistribution.Builder builder = ErrorCountDistribution.builder();

            public Handler() {
                super(ErrorSupport.SUPPORTED_TYPES);
            }
            
            @Override
            protected void process(PlayOutcome play, BaseSituation bases, int outs,
                    EventField field) {
                int errors = play.getNumberOfErrors();
                builder.add(play.getType(), bases.getOccupiedBases(), errors);
            }
            
            public ErrorCountDistribution getResult() {
                return builder.build();
            }
        }
    }

}
