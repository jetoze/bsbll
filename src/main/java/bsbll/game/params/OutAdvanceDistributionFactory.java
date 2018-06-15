package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.PlayByPlayFileUtils;

public abstract class OutAdvanceDistributionFactory {

    public abstract OutAdvanceDistribution createDistribution();
    
    // TODO: Implementation that reads from distributions that have been persisted to disk.
    
    /**
     * OutAdvanceDistributionFactory implementation that uses play-by-play data 
     * for the given year, provided by retrosheet.
     */
    public static OutAdvanceDistributionFactory retrosheet(Year year) {
        // TODO: Add option to pass in location of play-by-play files.
        requireNonNull(year);
        return new RetrosheetPlayByPlayFactory(year);
    }
    
    /**
     * Returns a factory that always produces distributions given by
     * {@link OutAdvanceDistribution#defaultAdvances()}.
     */
    public static OutAdvanceDistributionFactory defaultAdvances() {
        return DefaultDistributionFactory.INSTANCE;
    }

    private static final class DefaultDistributionFactory extends OutAdvanceDistributionFactory {
        private static final DefaultDistributionFactory INSTANCE = new DefaultDistributionFactory();

        @Override
        public OutAdvanceDistribution createDistribution() {
            return OutAdvanceDistribution.defaultAdvances();
        }
    }
    
    
    private static final class RetrosheetPlayByPlayFactory extends OutAdvanceDistributionFactory {
        private final Year year;
        
        public RetrosheetPlayByPlayFactory(Year year) {
            this.year = year;
        }

        @Override
        public OutAdvanceDistribution createDistribution() {
            Handler handler = new Handler();
            File folder = PlayByPlayFileUtils.getFolder(year);
            handler.parseAll(folder);
            return handler.getResult();
        }

        private static final class Handler extends DefaultGameHandler {
            private final OutAdvanceDistribution.Builder builder = OutAdvanceDistribution.builder();
            
            public Handler() {
                super(Handler::isOfInterest);
            }
            
            public OutAdvanceDistribution getResult() {
                return builder.build();
            }

            @Override
            protected void process(PlayOutcome play, BaseSituation bases, int outs, EventField field) {
                OutLocation location = getLocation(play, field);
                OutAdvanceKey key = OutAdvanceKey.of(play.getType(), location, outs);
                builder.add(key, bases, play.getAdvances());
            }

            private static OutLocation getLocation(PlayOutcome play, EventField field) {
                if (play.getType() == EventType.FIELDERS_CHOICE) {
                    // Perhaps not technically correct, but the retrosheet play-by-play files only
                    // contain a handful of FC7, FC8, FC9 events, and some of those could very well
                    // be plays made in the infield for all I know.
                    return OutLocation.INFIELD;
                }
                return field.isOutfieldOut()
                        ? OutLocation.OUTFIELD
                        : OutLocation.INFIELD;
            }

            private static boolean isOfInterest(PlayOutcome play) {
                return (play.getType() == EventType.OUT || play.getType() == EventType.FIELDERS_CHOICE)
                        && play.getNumberOfErrors() == 0;
            }
        }
    }
}
