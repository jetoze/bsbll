package bsbll.game;

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
}
