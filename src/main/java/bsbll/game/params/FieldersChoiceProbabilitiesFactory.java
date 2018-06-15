package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.pbpf.GameHandler;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import bsbll.research.pbpf.PlayByPlayFileUtils;

/**
 * Factory for creating {@code FieldersChoiceProbabilities}.
 */
public abstract class FieldersChoiceProbabilitiesFactory {

    public abstract FieldersChoiceProbabilities createProbabilities();
    
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
        
        private class Handler extends GameHandler {
            private final Multiset<Set<Base>> outsWithRunnersOnBase = HashMultiset.create();
            private final Multiset<Set<Base>> fieldersChoices = HashMultiset.create();
            
            private int playerId;
            
            @Override
            public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
                    ImmutableList<PlayOutcome> plays) {
                BaseSituation bases = BaseSituation.empty();
                for (PlayOutcome p : plays) {
                    if (!bases.isEmpty()) {
                        EnumSet<Base> occupied = bases.getOccupiedBases();
                        if (p.getType() == EventType.OUT) {
                            outsWithRunnersOnBase.add(occupied);
                        } else if (p.getType() == EventType.FIELDERS_CHOICE) {
                            fieldersChoices.add(occupied);
                        }
                    }
                    bases = bases.advanceRunners(nextBatter(), p.getAdvances()).getNewSituation();
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

            public FieldersChoiceProbabilities getResult() {
                FieldersChoiceProbabilities.Builder builder = FieldersChoiceProbabilities.builder();
                for (ImmutableSet<Base> bases : Base.occupiedBasesPossibilities()) {
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
