package bsbll.research;

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
            EventType eventType = EventTypeParser.parse(field);
            Advances advances = AdvanceFieldParser.parse(field.getAdvanceField(), eventType);
            int numberOfErrors = 0; // TODO: Implement me.
            
            // Here starts the special cases...
            if (eventType == EventType.CAUGHT_STEALING) {
                Base caughtAt = Base.fromChar(field.getBasicPlay().charAt(2));
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
                    if (isErrorOnCaughtStealing(field)) {
                        outcome = Outcome.SAFE;
                        ++numberOfErrors;
                    }
                    advances = advances.concat(new Advance(ranFrom, caughtAt, outcome));
                }
            }
            
            return new PlayOutcome(
                    eventType, 
                    advances, 
                    numberOfErrors);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid field: %s. Reported error: %s", 
                    field, e.getMessage()), e);
        }
    }
    
    private static boolean isErrorOnCaughtStealing(EventField field) {
        String basicPlay = field.getBasicPlay();
        assert basicPlay.startsWith("CS");
        return basicPlay.matches("CS[23H].*\\(\\dE\\d\\).*");
    }
    
    private EventParser() {
        // no reason (yet) to create instances of this class
    }

}
