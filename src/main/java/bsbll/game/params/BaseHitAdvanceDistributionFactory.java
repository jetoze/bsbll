package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.EventType;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.GameHandler;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
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

        private static final class Handler extends GameHandler {
            private final BaseHitAdvanceDistribution.Builder builder = BaseHitAdvanceDistribution.builder();
            private int playerId;
            
            public BaseHitAdvanceDistribution getResult() {
                return builder.build();
            }

            @Override
            public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
                    ImmutableList<PlayOutcome> plays) {
                BaseSituation baseSituation = BaseSituation.empty();
                for (PlayOutcome play : plays) {
                    Player batter = nextBatter();
                    if (play.isBaseHit() && play.getNumberOfErrors() == 0) {
                        update(play.getType(), baseSituation, play.getAdvances());
                    }
                    baseSituation = play.applyTo(batter, baseSituation);
                }
            }
            
            /**
             * We generate a new Player for each play. This is obviously not realistic,
             * but that is irrelevant - we just need Players to move around the bases.
             * See corresponding XXX comment in BaseSituation, about making that class generic.
             */
            private Player nextBatter() {
                ++playerId;
                return new Player(Integer.toString(playerId), "John Doe");
            }

            private void update(EventType typeOfHit, BaseSituation situation, Advances advances) {
                if (typeOfHit != EventType.HOMERUN) {
                    BaseHit hit = eventTypeToBaseHit(typeOfHit);
                    builder.add(hit, situation, advances);
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
