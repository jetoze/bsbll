package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.ParsedPlay;

public abstract class ErrorAdvanceDistributionFactory {

    public abstract ErrorAdvanceDistribution createDistribution(); 
    
    // TODO: Implementation that reads from distributions that have been persisted to disk.
    
    /**
     * ErrorAdvanceDistributionFactory implementation that uses play-by-play data 
     * for the given year, provided by retrosheet.
     */
    public static ErrorAdvanceDistributionFactory retrosheet(Year year) {
        // TODO: Add option to pass in location of play-by-play files.
        requireNonNull(year);
        return new RetrosheetFactory(year);
    }
    
    /**
     * Returns a factory that always produces distributions given by
     * {@link ErrorAdvanceDistribution#defaultAdvances()}.
     */
    public static ErrorAdvanceDistributionFactory defaultAdvances() {
        return DefaultDistributionFactory.INSTANCE;
    }

    private static final class DefaultDistributionFactory extends ErrorAdvanceDistributionFactory {
        private static final DefaultDistributionFactory INSTANCE = new DefaultDistributionFactory();

        @Override
        public ErrorAdvanceDistribution createDistribution() {
            return ErrorAdvanceDistribution.defaultAdvances();
        }
    }
    
    
    private static final class RetrosheetFactory extends ErrorAdvanceDistributionFactory {
        private final Year year;
        
        public RetrosheetFactory(Year year) {
            this.year = year;
        }

        @Override
        public ErrorAdvanceDistribution createDistribution() {
            Handler handler = new Handler();
            handler.parseAll(year);
            return handler.getResult();
        }
        
        private class Handler extends DefaultGameHandler {
            private final ErrorAdvanceDistribution.Builder builder = ErrorAdvanceDistribution.builder();
            
            public Handler() {
                super(p -> ErrorSupport.SUPPORTED_TYPES.contains(p.getType()) &&
                        p.getNumberOfErrors() > 0);
            }
            
            @Override
            protected void process(ParsedPlay play, BaseSituation bases, int outs) {
                ErrorAdvanceKey key = ErrorAdvanceKey.of(play.getType(), play.getNumberOfErrors(), outs);
                builder.add(key, bases, play.getAdvances());
            }

            public ErrorAdvanceDistribution getResult() {
                return builder.build();
            }
        }
    }
    
}
