package bsbll.research;

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
            EventType eventType = EventTypeParser.parse(field);
            Advances advances = AdvanceFieldParser.parse(field.getAdvanceField(), eventType);
            int numberOfErrors = 0; // TODO: Implement me.
            // TODO: Augment outs with outs that are not encoded in the advance field.
            // For example, the batter is out on a strikeout.
            return new PlayOutcome(
                    eventType, 
                    advances, 
                    numberOfErrors);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid field: %s. Reported error: %s", 
                    field, e.getMessage()), e);
        }
    }
    
    
    private EventParser() {
        // no reason (yet) to create instances of this class
    }

}
