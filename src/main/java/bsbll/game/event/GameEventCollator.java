package bsbll.game.event;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

public final class GameEventCollator {
    
    public static <T extends GameEvent> ImmutableList<CollatedEvent> collate(List<T> events,
                    Function<T, Player> playerExtractor) {
        Map<Player, List<T>> map = events.stream().collect(groupingBy(playerExtractor));
        ImmutableList.Builder<CollatedEvent> builder = ImmutableList.builder();
        for (Map.Entry<Player, List<T>> e : map.entrySet()) {
            int count = e.getValue().size();
            int seasonTotal = e.getValue().stream()
                    .mapToInt(GameEvent::getSeasonTotal)
                    .max()
                    .getAsInt();
            builder.add(new CollatedEvent(e.getKey(), count, seasonTotal));
        }
        return builder.build();
    }
    
    private GameEventCollator() {/**/}
    
    public static final class CollatedEvent {
        private final Player player;
        private final int count;
        private final int seasonTotal;

        public CollatedEvent(Player player, int count, int seasonTotal) {
            this.player = requireNonNull(player);
            this.count = checkPositive(count);
            this.seasonTotal = checkNotNegative(seasonTotal);
        }

        public Player getPlayer() {
            return player;
        }

        public int getCount() {
            return count;
        }

        public int getSeasonTotal() {
            return seasonTotal;
        }
    }
}
