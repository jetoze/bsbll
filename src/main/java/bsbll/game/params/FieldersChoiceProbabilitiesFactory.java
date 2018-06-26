package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import bsbll.game.play.EventType;
import bsbll.research.pbpf.DefaultGameHandler;
import bsbll.research.pbpf.ParsedPlay;
import bsbll.research.pbpf.PlayByPlayFileUtils;

/**
 * Factory for creating {@code FieldersChoiceProbabilities}.
 */
public abstract class FieldersChoiceProbabilitiesFactory {

    public abstract FieldersChoiceProbabilities createProbabilities();
    
    // TODO: Implementation that reads from distributions that have been persisted to disk.

    /**
     * Returns a factory that creates
     * {@link FieldersChoiceProbabilities#defaultValues() default}
     * probabilities.
     */
    public static FieldersChoiceProbabilitiesFactory defaultValues() {
        return DefaultValuesFactory.INSTANCE;
    }
    
    /**
     * Returns a factory that creates FielderChoiceProbabilities based on the
     * retrosheet play-by-play data for the given year.
     */
    public static FieldersChoiceProbabilitiesFactory retrosheet(Year year) {
        // TODO: Add option to pass in location of play-by-play files.
        requireNonNull(year);
        return new RetrosheetFactory(year);
    }
    
    
    private static final class DefaultValuesFactory extends FieldersChoiceProbabilitiesFactory {
        public static final DefaultValuesFactory INSTANCE = new DefaultValuesFactory();

        @Override
        public FieldersChoiceProbabilities createProbabilities() {
            return FieldersChoiceProbabilities.defaultValues();
        }
    }
    
    
    private static final class RetrosheetFactory extends FieldersChoiceProbabilitiesFactory {
        private final Year year;
        
        public RetrosheetFactory(Year year) {
            this.year = year;
        }
        
        @Override
        public FieldersChoiceProbabilities createProbabilities() {
            File folder = PlayByPlayFileUtils.getFolder(year);
            Handler handler = new Handler();
            handler.parseAll(folder);
            return handler.getResult();
        }
        
        private class Handler extends DefaultGameHandler {
            private final Multiset<OccupiedBases> outsWithRunnersOnBase = HashMultiset.create();
            private final Multiset<OccupiedBases> fieldersChoices = HashMultiset.create();
            
            @Override
            protected void process(ParsedPlay play, BaseSituation bases, int outs) {
                if (!bases.areEmpty()) {
                    evaluate(play, bases);
                }
            }

            private void evaluate(ParsedPlay play, BaseSituation bases) {
                OccupiedBases occupied = bases.getOccupiedBases();
                if (play.isInfieldOut()) {
                    outsWithRunnersOnBase.add(occupied);
                } else if (play.getType() == EventType.FIELDERS_CHOICE) {
                    fieldersChoices.add(occupied);
                }
            }

            public FieldersChoiceProbabilities getResult() {
                FieldersChoiceProbabilities.Builder builder = FieldersChoiceProbabilities.builder();
                for (OccupiedBases bases : OccupiedBases.values()) {
                    int outs = outsWithRunnersOnBase.count(bases);
                    if (outs > 0) {
                        int fcs = fieldersChoices.count(bases);
                        builder.add(bases, outs, fcs);
                    }
                }
                return builder.build();
            }
        }
    }
}
