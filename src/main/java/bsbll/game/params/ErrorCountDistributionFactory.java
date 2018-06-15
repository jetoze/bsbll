package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.PlayByPlayFileUtils;

public abstract class ErrorCountDistributionFactory {

    protected abstract ErrorCountDistribution createDistribution();

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
        protected ErrorCountDistribution createDistribution() {
            return ErrorCountDistribution.noErrors();
        }
    }
    
    
    private static final class RetrosheetFactory extends ErrorCountDistributionFactory {
        private final Year year;
        
        public RetrosheetFactory(Year year) {
            this.year = year;
        }
        
        @Override
        protected ErrorCountDistribution createDistribution() {
            Handler handler = new Handler();
            File folder = PlayByPlayFileUtils.getFolder(year);
            handler.parseAll(folder);
            return handler.getResult();
        }
        
        private class Handler extends DefaultGameHandler {
            private final ErrorCountDistribution.Builder builder = ErrorCountDistribution.builder();

            public Handler() {
                super(p -> ErrorSupport.isSupported(p.getType()));
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
