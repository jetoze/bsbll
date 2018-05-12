package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
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
        EventType eventType = EventTypeParser.parse(field);
        AdvanceFieldParser.Result advanceFieldResult = parseAdvance(field, eventType);
        int numberOfErrors = 0; // TODO: Implement me.
        return new PlayOutcome(
                eventType, 
                advanceFieldResult.getAdvances(), 
                advanceFieldResult.getOuts(), 
                numberOfErrors);
    }
    
    private static AdvanceFieldParser.Result parseAdvance(String field, EventType eventType) {
        String advanceField = getAdvanceField(field);
        return AdvanceFieldParser.parse(advanceField, eventType);
    }
    
    private static String getAdvanceField(String field) {
        int index = field.indexOf('.');
        if (index == -1) {
            return "";
        }
        checkArgument(index < field.length() - 3, "Invalid event field: " + field);
        return field.substring(index + 1);
    }
    
    
    private EventParser() {
        // no reason (yet) to create instances of this class
    }

}
