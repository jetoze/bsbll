package bsbll.game;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import tzeth.collections.ImCollectors;

/**
 * A collection of events that occurred in a game.
 */
@Immutable
public final class GameEvents {
    // TODO: Store the event in a map, with the type as key? The main operation on a
    // GameEvents instance after instantiation is lookups.
    private final ImmutableList<? extends GameEvent> events;
    
    public GameEvents(List<? extends GameEvent> events) {
        this.events = ImmutableList.copyOf(events);
    }
    
    public static GameEvents of(List<? extends GameEvent> events) {
        return new GameEvents(events);
    }
    
    public <T extends GameEvent> ImmutableList<T> getEvents(Class<T> type) {
        return this.events.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(ImCollectors.toList());
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<GameEvent> events = new ArrayList<>();
        
        public GameEvents build() {
            return new GameEvents(events);
        }
        
        public Builder add(GameEvent e) {
            requireNonNull(e);
            this.events.add(e);
            return this;
        }

        /*
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
        */
    }
}
