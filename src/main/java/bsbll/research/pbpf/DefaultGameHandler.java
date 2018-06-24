package bsbll.research.pbpf;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.BaseRunner;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public abstract class DefaultGameHandler extends GameHandler {
    private final Predicate<PlayOutcome> interestingPlayPredicate;
    private final Player pitcher = new Player("pitcher", "Joe Wood");
    private int playerId;
    
    protected DefaultGameHandler() {
        this(p -> true);
    }
    
    protected DefaultGameHandler(EventType type) {
        this(p -> p.getType() == type);
        requireNonNull(type);
    }
    
    protected DefaultGameHandler(Set<EventType> types) {
        this(p -> types.contains(p.getType()));
        requireNonNull(types);
        checkArgument(!types.isEmpty());
    }
    
    protected DefaultGameHandler(Predicate<PlayOutcome> interestingPlayPredicate) {
        this.interestingPlayPredicate = requireNonNull(interestingPlayPredicate);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public final void onEndOfInning(Inning inning, ImmutableList<ParsedPlay> plays) {
        BaseSituation bases = BaseSituation.empty();
        int outs = 0;
        for (ParsedPlay play : plays) {
            if (interestingPlayPredicate.test(play.getOutcome())) {
                process(play, bases, outs);
            }
            outs += play.getNumberOfOuts();
            bases = bases.advanceRunners(nextBatter(), play.getAdvances()).getNewSituation();
        }
    }
    
    /**
     * Processes the given play.
     * 
     * @param play
     *            the ParsedPlay
     * @param bases
     *            the base situation at the time of the play
     * @param outs
     *            the number of outs at the time of the play
     */
    protected abstract void process(ParsedPlay play, BaseSituation bases, int outs);
    
    /**
     * We generate a new Player for each play. This is obviously not realistic,
     * but that is irrelevant - we just need Players to move around the bases.
     * See corresponding XXX comment in BaseSituation, about making that class generic.
     */
    private BaseRunner nextBatter() {
        ++playerId;
        return new BaseRunner(new Player(Integer.toString(playerId), "John Doe"), pitcher);
    }

    public final void parseAll(Year year) {
        File folder = PlayByPlayFileUtils.getFolder(year);
        parseAll(folder);
    }

}
