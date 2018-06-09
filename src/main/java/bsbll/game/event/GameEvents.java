package bsbll.game.event;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.game.Inning;
import tzeth.collections.ImCollectors;

/**
 * A collection of events that occurred in a game.
 */
@Immutable
public final class GameEvents {
    // TODO: Store the event in a map, with the type as key? The main operation on a
    // GameEvents instance after instantiation is lookups.
    private final ImmutableList<GameEvent> events;
    
    public GameEvents(List<? extends GameEvent> events) {
        this.events = ImmutableList.copyOf(events);
    }
    
    public static GameEvents of(List<? extends GameEvent> events) {
        return new GameEvents(events);
    }
    
    public boolean isEmpty() {
        return events.isEmpty();
    }
    
    public <T extends GameEvent> ImmutableList<T> getEvents(Class<T> type) {
        return events.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(ImCollectors.toList());
    }
    
    public GameEvents subset(Predicate<? super GameEvent> filter) {
        return new GameEvents(events.stream()
                .filter(filter)
                .collect(ImCollectors.toList()));
    }
    
    public ImmutableMap<Inning.Half, GameEvents> splitByInningHalf() {
        GameEvents top = subset(e -> e.getInning().isTop());
        GameEvents bottom = subset(e -> e.getInning().isBottom());
        return ImmutableMap.of(Inning.Half.TOP, top, Inning.Half.BOTTOM, bottom);
    }
    
    @Override
    public String toString() {
        return events.size() + " GameEvent(s)";
    }
}
