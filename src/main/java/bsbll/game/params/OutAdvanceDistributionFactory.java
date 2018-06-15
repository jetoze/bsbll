package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.pbpf.GameHandler;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
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

        private static final class Handler extends GameHandler {
            private final OutAdvanceDistribution.Builder builder = OutAdvanceDistribution.builder();
            private int playerId;
            
            public OutAdvanceDistribution getResult() {
                return builder.build();
            }

            @Override
            public void onEndOfInning(Inning inning, 
                                      ImmutableList<EventField> fields,
                                      ImmutableList<PlayOutcome> plays) {
                BaseSituation baseSituation = BaseSituation.empty();
                int outs = 0;
                Iterator<EventField> itF = fields.iterator();
                for (PlayOutcome play : plays) {
                    EventField field = itF.next();
                    Player batter = nextBatter();
                    if (isOfInterest(play)) {
                        update(play, field, baseSituation, outs);
                    }
                    baseSituation = play.applyTo(batter, baseSituation);
                    outs += play.getNumberOfOuts();
                }
            }

            public boolean isOfInterest(PlayOutcome play) {
                return (play.getType() == EventType.OUT || play.getType() == EventType.FIELDERS_CHOICE)
                        && play.getNumberOfErrors() == 0;
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

            private void update(PlayOutcome play, EventField field, BaseSituation situation, int outs) {
                OutLocation location = getLocation(play, field);
                OutAdvanceKey key = OutAdvanceKey.of(play.getType(), location, outs);
                builder.add(key, situation, play.getAdvances());
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
        }
    }
}
