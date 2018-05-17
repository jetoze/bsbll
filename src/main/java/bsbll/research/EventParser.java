package bsbll.research;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import bsbll.research.Advance.Outcome;

/**
 * Parses the event field of a retrosheet play-by-play file and returns the corresponding PlayOutcome.
 */
public final class EventParser {
    public static PlayOutcome parse(String s) {
        EventField field = EventField.fromString(s);
        return parse(field);
    }
    
    /**
     * Parses the event field and returns the corresponding PlayOutcome.
     * 
     * @param field
     *            the event field
     * @return the corresponding PlayOutcome
     * @throws IllegalArgumentException
     *             if the field is not a valid event field
     */
    public static PlayOutcome parse(EventField field) {
        try {
            EventParser parser = new EventParser(field);
            return parser.parse();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid field: %s. Reported error: %s", 
                    field, e.getMessage()), e);
        }
    }
    
    private final EventField field;
    private Advances advances;
    private int numberOfErrors;
    
    private EventParser(EventField field) {
        this.field = requireNonNull(field);
    }

    private PlayOutcome parse() {
        EventType eventType = EventTypeParser.parse(field);
        this.advances = AdvanceFieldParser.parse(field.getAdvanceField(), eventType);
        
        // Here starts the special cases...
        switch (eventType) {
        case STOLEN_BASE:
            handleStolenBase();
            break;
        case CAUGHT_STEALING:
            handleCaughtStealing();
            break;
        case PICKED_OFF:
            handlePickoff();
            break;
        case STRIKEOUT:
            handleAdditionalEvent();
            break;
        case WALK:
            handleAdditionalEvent();
            break;
        default:
            // no additional processing needed
        }
        
        numberOfErrors += field.getAdvanceField().countAllErrors();
        
        return new PlayOutcome(
                eventType, 
                advances, 
                numberOfErrors);
    }
    
    private void handleStolenBase() {
        handleStolenBase(field.getBasicPlay());
    }
    
    private void handleStolenBase(String marker) {
        String[] parts = marker.split(";");
        for (String p : parts) {
            if (p.startsWith("SB")) {
                Base stolen = Base.fromChar(p.charAt(2));
                Base from = stolen.preceding();
                if (this.advances.contains(from)) {
                    Advance a = this.advances.getAdvanceFrom(from);
                    if (a.to().compareTo(stolen) > 0) {
                        // Indicates an error was made, allowing the runner to take additional bases.
                        // Increase the error count, unless the error is already given explicitly
                        // in the advance field itself (they are counted separately)
                        if (field.getAdvanceField().countErrors(from) == 0) {
                            ++numberOfErrors;
                        }
                    }
                } else {
                    addAdvance(Advance.safe(from, stolen));
                }
            }
        }
    }

    private void handleCaughtStealing() {
        handleCaughtStealing(field.getBasicPlay());
    }

    private void handleCaughtStealing(String marker) {
        assert marker.startsWith("CS");
        Base caughtAt = Base.fromChar(marker.charAt(2));
        Base ranFrom = caughtAt.preceding();
        if (advances.contains(ranFrom)) {
            if (advances.didRunnerAdvance(ranFrom)) {
                // This is the case where an error negates the CS.
                ++numberOfErrors;
            }
        } else {
            // TODO: Look for the presence of a marker like (2E6) in the basic
            // part of the field. This indicates an error, which negates the CS,
            // while the advancement is NOT given explicitly in the advancement field
            // (which is covered above).
            Outcome outcome = Outcome.OUT;
            if (isErrorOnCaughtStealing(marker)) {
                outcome = Outcome.SAFE;
                ++numberOfErrors;
            }
            addAdvance(new Advance(ranFrom, caughtAt, outcome));
        }
    }
    
    private static boolean isErrorOnCaughtStealing(String basicPlay) {
        assert basicPlay.startsWith("CS");
        return basicPlay.matches("CS[23H].*\\(\\dE\\d\\).*");
    }

    private void handlePickoff() {
        handlePickoff(this.field.getBasicPlay());
    }
    
    private void handlePickoff(String marker) {
        assert marker.startsWith("PO");
        Base from;
        Base to;
        if (marker.startsWith("POCS")) {
            to = Base.fromChar(marker.charAt(4));
            from = to.preceding();
        } else {
            from = Base.fromChar(marker.charAt(2));
            to = from;
        }
        if (!advances.contains(from)) {
            addAdvance(Advance.out(from, to));
        }
    }
    
    private void handleAdditionalEvent() {
        int sep = this.field.getBasicPlay().indexOf('+');
        if (sep == -1) {
            return;
        }
        String additionalEventString = this.field.getBasicPlay().substring(sep + 1);
        EventType additionalEventType = EventTypeParser.parse(additionalEventString);
        switch (additionalEventType) {
        case STOLEN_BASE:
            handleStolenBase(additionalEventString);
            break;
        case CAUGHT_STEALING:
            handleCaughtStealing(additionalEventString);
            break;
        case PICKED_OFF:
            handlePickoff(additionalEventString);
            break;
        case WILD_PITCH:
            // Nothing to do(?). All relevant side effects are handled via the 
            // advance field.
            break;
        case PASSED_BALL:
            // Same as WILD_PITCH
            break;
        default:
            throw new RuntimeException("Not implemented yet");
        }
    }
    
    private void addAdvance(Advance a) {
        checkState(this.advances != null);
        this.advances = this.advances.concat(a);
    }
    
}
