package bsbll.player;

import static java.util.Objects.requireNonNull;

public final class Player {
    private final PlayerId id;
    private final PlayerName name;
    // TODO: At the very minimum we need some sort of Name abstraction.
    
    public Player(PlayerId id, PlayerName name) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
    }
    
    public Player(String id, String fullName) {
        this.id = PlayerId.of(id);
        this.name = PlayerName.fromFullName(fullName);
    }

    public PlayerId getId() {
        return id;
    }
    
    public PlayerName getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s]", name.getFullName(), id);
    }
}
