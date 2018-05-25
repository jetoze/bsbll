package bsbll.team;

import java.util.Objects;

import javax.annotation.Nullable;

public final class GamesBehind implements Comparable<GamesBehind> {
    private final int halfGames;
    
    public GamesBehind(int halfGames) {
        this.halfGames = halfGames;
    }
    
    @Override
    public int compareTo(GamesBehind o) {
        return Integer.compare(this.halfGames, o.halfGames);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) ||
                ((obj instanceof GamesBehind) && (this.halfGames == ((GamesBehind) obj).halfGames));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.halfGames);
    }

    @Override
    public String toString() {
        if (halfGames == 0) {
            return "--";
        }
        StringBuilder sb = new StringBuilder();
        if (halfGames < 0) {
            sb.append("-");
        }
        sb.append(halfGames / 2)
            .append(".").append(((halfGames % 2) == 0 ? "0" : "5"));
        return sb.toString();
    }
}
