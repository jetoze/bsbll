package bsbll.research.pbpf;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import bsbll.bases.Advances;
import bsbll.bases.BaseSituation;
import bsbll.game.BaseRunner;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.PlayerId;
import bsbll.research.EventField;

/**
 * Represents a parsed play entry from a play-by-play file.
 */
@Immutable
public final class ParsedPlay {
    private final PlayerId pitcherId;
    private final PlayerId batterId;
    private final EventField eventField;
    private final PlayOutcome outcome;

    public ParsedPlay(PlayerId batterId, 
                      PlayerId pitcherId, 
                      EventField eventField,
                      PlayOutcome outcome) {
        this.pitcherId = requireNonNull(pitcherId);
        this.batterId = requireNonNull(batterId);
        this.eventField = requireNonNull(eventField);
        this.outcome = requireNonNull(outcome);
    }

    public PlayerId getPitcherId() {
        return pitcherId;
    }

    public PlayerId getBatterId() {
        return batterId;
    }

    public EventField getEventField() {
        return eventField;
    }

    public PlayOutcome getOutcome() {
        return outcome;
    }
    
    public EventType getType() {
        return outcome.getType();
    }
    
    public boolean isHomerun() {
        return outcome.isHomerun();
    }

    public boolean isInfieldOut() {
        return getType() == EventType.OUT && !isOutfieldOut();
    }
    
    public boolean isOutfieldOut() {
        return eventField.isOutfieldOut();
    }
    
    public boolean isSacrificeFly() {
        return eventField.isSacrificeFly();
    }
    
    public int getNumberOfRuns() {
        return outcome.getNumberOfRuns();
    }
    
    public int getNumberOfOuts() {
        return outcome.getNumberOfOuts();
    }
    
    public int getNumberOfErrors() {
        return outcome.getNumberOfErrors();
    }
    
    public Advances getAdvances() {
        return outcome.getAdvances();
    }

    public BaseSituation applyTo(BaseRunner batter, BaseSituation baseSituation) {
        return outcome.applyTo(batter, baseSituation);
    }
}
