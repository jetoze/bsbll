package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;
import bsbll.player.PlayerFactory;

public final class Roster {
    private final ImmutableList<Player> batters;
    private final Rotation rotation;

    public Roster(List<Player> batters, List<Player> startingPitchers) {
        checkArgument(batters.size() >= 8, "Must provide at least eight batters, got %s", batters.size());
        checkArgument(startingPitchers.size() >= 1, "Must provide at least one starting pitcher");
        this.batters = ImmutableList.copyOf(batters);
        this.rotation = new Rotation(startingPitchers);
    }

    /**
     * Gets the lineup for a new game.
     * <p>
     * A new {@code Lineup} instance is returned by each call.
     */
    public Lineup getLineup() {
        Player pitcher = rotation.nextStartingPitcher();
        BattingOrder bo = createBattingOrder(pitcher);
        return new Lineup(bo, pitcher);
    }

    private BattingOrder createBattingOrder(Player pitcher) {
        List<Player> bo = new ArrayList<>(batters);
        bo.add(pitcher);
        return new BattingOrder(bo);
    }
    
    public static Builder builder() {
        return builder(PlayerFactory.defaultFactory());
    }
    
    public static Builder builder(PlayerFactory playerFactory) {
        return new Builder(playerFactory);
    }
    
    
    public static final class Builder {
        private final PlayerFactory playerFactory;
        private final List<Player> batters = new ArrayList<>();
        private final List<Player> startingPitchers = new ArrayList<>();
        
        public Builder(PlayerFactory playerFactory) {
            this.playerFactory = requireNonNull(playerFactory);
        }

        public Builder withBatter(String id) {
            this.batters.add(getPlayer(id));
            return this;
        }
        
        public Builder withBatters(String... ids) {
            Arrays.stream(ids).forEach(this::withBatter);
            return this;
        }
        
        public Builder withStartingPitcher(String id) {
            this.startingPitchers.add(getPlayer(id));
            return this;
        }
        
        public Builder withStartingPitchers(String... ids) {
            Arrays.stream(ids).forEach(this::withStartingPitcher);
            return this;
        }

        private Player getPlayer(String id) {
            requireNonNull(id);
            return playerFactory.getPlayer(id);
        }
        
        public Roster build() {
            return new Roster(batters, startingPitchers);
        }
    }
}
