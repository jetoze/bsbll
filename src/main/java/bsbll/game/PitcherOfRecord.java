package bsbll.game;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import bsbll.player.Player;
import bsbll.stats.WinLossRecord;

/**
 * The winning or losing pitcher in a game.
 */
@Immutable
public final class PitcherOfRecord {
    private final Player pitcher;
    private final Decision decision;
    private final WinLossRecord record;

    public PitcherOfRecord(Player pitcher, Decision decision, WinLossRecord record) {
        this.pitcher = requireNonNull(pitcher);
        this.decision = requireNonNull(decision);
        this.record = requireNonNull(record);
    }

    public Player getPitcher() {
        return pitcher;
    }

    public Decision getDecision() {
        return decision;
    }

    /**
     * The pitcher's record including this decision.
     */
    public WinLossRecord getRecord() {
        return record;
    }
}
