package bsbll.game.event;

import bsbll.game.Inning;

/**
 * Represents a event that took place during a game, e.g. a homerun, that should be reported
 * in the box score.
 */
public interface GameEvent {
    Inning getInning();
    int getSeasonTotal();
}
