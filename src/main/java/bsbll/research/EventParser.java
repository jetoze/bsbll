package bsbll.research;

import static tzeth.preconds.MorePreconditions.checkNotBlank;

/**
 * Parses the event field of a retrosheet play-by-play file and returns the corresponding PlayOutcome.
 */
public final class EventParser {
    /**
     * Parses the event field and returns the corresponding PlayOutcome.
     * 
     * @param field
     *            the event field
     * @return the corresponding PlayOutcome
     * @throws IllegalArgumentException
     *             if the field is not a valid event field
     */
    public static PlayOutcome parse(String field) {
        checkNotBlank(field);
        if (field.startsWith("S")) {
            return PlayOutcome.builder()
                    .withType(EventType.SINGLE)
                    .withAdvance(new Advance(Base.HOME, Base.FIRST))
                    .build();
        }
        throw new IllegalArgumentException("Invalid event field: " + field);
    }
    
    
    private EventParser() {
        // no reason (yet) to create instances of this class
    }

}
