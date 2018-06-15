package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

import bsbll.Year;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.PlayByPlayFileUtils;

/**
 * A factory for constructing {@link BaseHitAdvanceDistribution}s. This can be
 * used to create distributions in bulk, rather than manually via the simple
 * {@link BaseHitAdvanceDistribution.Builder}.
 */
public abstract class BaseHitAdvanceDistributionFactory {

    public abstract BaseHitAdvanceDistribution createDistribution();
    
    // TODO: Implementation that reads from distributions that have been persisted to disk.
    
    /**
     * BaseHitAdvanceDistributionFactory implementation that uses play-by-play data 
     * for the given year, provided by retrosheet.
     */
    public static BaseHitAdvanceDistributionFactory retrosheet(Year year) {
        // TODO: Add option to pass in location of play-by-play files.
        requireNonNull(year);
        return new RetrosheetPlayByPlayFactory(year);
    }
    
    /**
     * Returns a factory that always produces distributions given by
     * {@link BaseHitAdvanceDistribution#defaultAdvances()}.
     */
    public static BaseHitAdvanceDistributionFactory defaultAdvances() {
        return DefaultDistributionFactory.INSTANCE;
    }

    
    private static final class DefaultDistributionFactory extends BaseHitAdvanceDistributionFactory {
        private static final DefaultDistributionFactory INSTANCE = new DefaultDistributionFactory();

        @Override
        public BaseHitAdvanceDistribution createDistribution() {
            return BaseHitAdvanceDistribution.defaultAdvances();
        }
    }
    
    
    private static final class RetrosheetPlayByPlayFactory extends BaseHitAdvanceDistributionFactory {
        private final Year year;
        
        public RetrosheetPlayByPlayFactory(Year year) {
            this.year = year;
        }

        @Override
        public BaseHitAdvanceDistribution createDistribution() {
            Handler handler = new Handler();
            File folder = PlayByPlayFileUtils.getFolder(year);
            handler.parseAll(folder);
            return handler.getResult();
        }

        private static final class Handler extends DefaultGameHandler {
            private final BaseHitAdvanceDistribution.Builder builder = BaseHitAdvanceDistribution.builder();

            public Handler() {
                super(p -> p.isBaseHit() && p.getNumberOfErrors() == 0);
            }
            
            public BaseHitAdvanceDistribution getResult() {
                return builder.build();
            }

            @Override
            protected void process(PlayOutcome play, BaseSituation bases, int outs, EventField field) {
                EventType typeOfHit = play.getType();
                if (typeOfHit != EventType.HOMERUN) {
                    BaseHit hit = eventTypeToBaseHit(typeOfHit);
                    builder.add(hit, bases, play.getAdvances());
                }
            }

            private static BaseHit eventTypeToBaseHit(EventType type) {
                assert type.isHit();
                switch (type) {
                case SINGLE:
                    return BaseHit.SINGLE;
                case DOUBLE:
                    return BaseHit.DOUBLE;
                case TRIPLE:
                    return BaseHit.TRIPLE;
                case HOMERUN:
                    return BaseHit.HOMERUN;
                default:
                    throw new AssertionError("Unexpected event type: " + type);
                }
            }
        }
    }
}
