package bsbll.player;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PlayerName {
    private final String first;
    private final String last;

    public PlayerName(String first, String last) {
        this.first = requireNonNull(first);
        this.last = requireNonNull(last);
    }
    
    public static PlayerName fromFullName(String name) {
        String[] parts = name.split(" ");
        checkArgument(parts.length == 2, "Not a valid name: " + name);
        return new PlayerName(parts[0], parts[1]);
    }
    
    public static PlayerName fromFirstAndLastName(String first, String last) {
        return new PlayerName(first, last);
    }
    
    public String getFirstName() {
        return first;
    }
    
    public String getLastName() {
        return last;
    }
    
    public String getShortForm() {
        return last + ", " + first.charAt(0);
    }
    
    public String getFullName() {
        return first + " " + last;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PlayerName) {
            PlayerName that = (PlayerName) obj;
            return this.first.equals(that.first) && this.last.equals(that.last);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash(first, last);
    }
}
