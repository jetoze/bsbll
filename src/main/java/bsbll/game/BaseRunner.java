package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import bsbll.player.Player;

/**
 * Represents a base runner.
 */
@Immutable
public final class BaseRunner {
    private final Player runner;
    private final Player responsiblePitcher;
    
    public BaseRunner(Player runner, Player responsiblePitcher) {
        this.runner = requireNonNull(runner);
        this.responsiblePitcher = requireNonNull(responsiblePitcher);
        checkArgument(runner != responsiblePitcher);
    }

    public Player getRunner() {
        return runner;
    }

    public Player getResponsiblePitcher() {
        return responsiblePitcher;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BaseRunner) {
            BaseRunner that = (BaseRunner) obj;
            return this.runner.equals(that.runner) && this.responsiblePitcher.equals(that.responsiblePitcher);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(runner, responsiblePitcher);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", runner, responsiblePitcher);
    }
}
