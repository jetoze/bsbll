package bsbll.research;

import static java.util.Objects.requireNonNull;

public final class Player {
    private final String name;
    
    public Player(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
