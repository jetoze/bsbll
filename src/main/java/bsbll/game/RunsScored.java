package bsbll.game;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.game.RunsScored.Run;
import bsbll.player.Player;

/**
 * The runs that scored in a game, in the order that they scored.
 */
@Immutable
public final class RunsScored implements Iterable<Run> {
    private static final RunsScored EMPTY = new RunsScored(ImmutableList.of());
    
    private final ImmutableList<Run> runs;
    
    public RunsScored(List<Run> runs) {
        this.runs = ImmutableList.copyOf(runs);
    }
    
    public static RunsScored of(List<Run> runs) {
        return new RunsScored(runs);
    }
    
    public static RunsScored empty() {
        return EMPTY;
    }
    
    @Override
    public Iterator<Run> iterator() {
        return runs.iterator();
    }
    
    public Stream<Run> stream() {
        return runs.stream();
    }

    // TODO: This method is really the only reason this class needs to exist.
    // Is it worth it? The alternative would be to simply use ImmutableList<Run>,
    // and let some other class be responsible for figuring out losing (and 
    // winning) pitcher.
    public Player getLosingPitcher() {
        int topScore = 0;
        int bottomScore = 0;
        boolean tie = true;
        Player losingPitcher = null;
        for (Run r : runs) {
            if (tie) {
                losingPitcher = r.responsiblePitcher;
            }
            if (r.inning.isTop()) {
                ++topScore;
            } else {
                ++bottomScore;
            }
            tie = (topScore == bottomScore);
            if (tie) {
                losingPitcher = null;
            }
        }
        checkState(losingPitcher != null, "The game was a tie");
        return losingPitcher;
    }
    
    @Immutable
    public static final class Run {
        private final Inning inning;
        private final Player runner;
        private final Player responsiblePitcher;

        public Run(Inning inning, Player runner, Player responsiblePitcher) {
            this.inning = requireNonNull(inning);
            this.runner = requireNonNull(runner);
            this.responsiblePitcher = requireNonNull(responsiblePitcher);
        }

        /**
         * The inning in which the run scored.
         */
        public Inning getInning() {
            return inning;
        }

        /**
         * The runner that scored.
         */
        public Player getRunner() {
            return runner;
        }

        /**
         * The pitcher that was responsible for putting the runner on base.
         */
        public Player getResponsiblePitcher() {
            return responsiblePitcher;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s (%s)", inning, runner.getName().getShortForm(), 
                    responsiblePitcher.getName().getShortForm());
        }
    }
}
