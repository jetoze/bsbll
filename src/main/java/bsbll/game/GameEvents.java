package bsbll.game;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

/**
 * A collection of events that occurred in a game.
 */
@Immutable
public final class GameEvents {
    private final ImmutableList<HomerunEvent> homeruns;
    private final ImmutableList<DoubleEvent> doubles;
    private final ImmutableList<TripleEvent> triples;
    // TODO: Add things like Stolen Bases, Errors, Double Plays
    
    public GameEvents(List<HomerunEvent> homeruns, List<DoubleEvent> doubles, List<TripleEvent> triples) {
        this.homeruns = ImmutableList.copyOf(homeruns);
        this.doubles = ImmutableList.copyOf(doubles);
        this.triples = ImmutableList.copyOf(triples);
    }

    public ImmutableCollection<HomerunEvent> getHomeruns() {
        return homeruns;
    }
    
    public ImmutableCollection<DoubleEvent> getDoubles() {
        return doubles;
    }
    
    public ImmutableCollection<TripleEvent> getTriples() {
        return triples;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<HomerunEvent> homeruns = new ArrayList<>();
        private final List<DoubleEvent> doubles = new ArrayList<>();
        private final List<TripleEvent> triples = new ArrayList<>();
        
        public GameEvents build() {
            return new GameEvents(homeruns, doubles, triples);
        }
        
        public Builder addHomerun(HomerunEvent hr) {
            return addXBH(hr, this.homeruns::add);
        }
        
        public Builder addDouble(DoubleEvent db) {
            return addXBH(db, this.doubles::add);
        }
        
        public Builder addTriple(TripleEvent tp) {
            return addXBH(tp, this.triples::add);
        }
        
        private <T extends ExtraBaseHitEvent> Builder addXBH(T event, Consumer<T> bin) {
            requireNonNull(event);
            bin.accept(event);
            return this;
        }
        
        public void examine(Outcome outcome, Player batter, Player pitcher, int inning, 
                int outs, BaseSituation baseSituation) {
            switch (outcome) {
            case DOUBLE:
                addDouble(new DoubleEvent(batter, pitcher));
                break;
            case TRIPLE:
                addTriple(new TripleEvent(batter, pitcher));
                break;
            case HOMERUN:
                addHomerun(HomerunEvent.builder(batter, pitcher)
                        .inInning(inning)
                        .withOuts(outs)
                        .withRunnersOn(baseSituation.getNumberOfRunners())
                        .build());
                break;
            default:
                // Not of interest.
            }
        }
    }
}
