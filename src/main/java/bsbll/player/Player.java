package bsbll.player;

import static java.util.Objects.requireNonNull;

public final class Player {
    private final PlayerId id;
    // TODO: At the very minimum we need some sort of Name abstraction.
    
    public Player(PlayerId id) {
        this.id = requireNonNull(id);
    }

    public PlayerId getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return id.toString();
    }
}
