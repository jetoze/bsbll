package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

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
        
        private int playerId;
        
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
        
        private class Handler extends GameHandler {
            private final ErrorCountDistribution.Builder builder = ErrorCountDistribution.builder();

            @Override
            public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
                    ImmutableList<PlayOutcome> plays) {
                BaseSituation bases = BaseSituation.empty();
                for  (PlayOutcome p : plays) {
                    if (isApplicable(p)) {
                        int errors = p.getNumberOfErrors();
                        builder.add(p.getType(), bases.getOccupiedBases(), errors);
                    }
                    bases = bases.advanceRunners(nextBatter(), p.getAdvances()).getNewSituation();
                }
            }

            private boolean isApplicable(PlayOutcome p) {
                EventType type = p.getType();
                return (type == EventType.OUT) ||
                        (type == EventType.SINGLE) ||
                        (type == EventType.DOUBLE) ||
                        (type == EventType.TRIPLE);
            }
            
            public ErrorCountDistribution getResult() {
                return builder.build();
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
    }

}
