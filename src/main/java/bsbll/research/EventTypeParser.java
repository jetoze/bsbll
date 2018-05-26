package bsbll.research;

import static bsbll.research.EventType.*;

import java.util.function.Predicate;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

/**
 * Parses the event field of a retrosheet play-by-play file and returns the corresponding event type.
 */
public final class EventTypeParser {
    public static EventType parse(String s) {
        return parse(EventField.fromString(s));
    }
    
    /**
     * Parses the event field and returns the corresponding event type.
     * 
     * @param field
     *            the event field
     * @return the corresponding event type
     * @throws IllegalArgumentException
     *             if the field is not a valid event field
     */
    public static EventType parse(EventField field) {
        String basic = field.getBasicPlay();
        char first = basic.charAt(0);
        switch (first) {
        case 'S':
            if (matchWithOptionalFielderSuffix(basic, "S")) {
                return SINGLE;
            } else if (matchWithOptionalSuffix(basic, "SB", EventTypeParser::isStealableBase)) {
                return EventType.STOLEN_BASE;
            }
            break;
        case 'D':
            if (matchWithOptionalFielderSuffix(basic, "D") ||
                    matchWithOptionalFielderSuffix(basic, "DGR")) {
                return DOUBLE;
            } else if (basic.equals("DI")) {
                return DEFENSIVE_INDIFFERENCE;
            }
            break;
        case 'T':
            if (matchWithOptionalFielderSuffix(basic, "T")) {
                return TRIPLE;
            }
            break;
        case 'H': {
            if (matchWithOptionalFielderSuffix(basic, "H") ||
                    matchWithOptionalFielderSuffix(basic, "HR")) {
                return HOMERUN;
            } else if (basic.equals("HP")) {
                return HIT_BY_PITCH;
            }
            break;
        }
        case 'K':
            if (matchWithOptionalPlus(basic, "K") ||
                    matchWithOptionalFielderSuffix(basic, "K")) {
                return STRIKEOUT;
            }
            break;
        case 'W':
            if (matchWithOptionalPlus(basic, "W")) {
                return WALK;
            } else if (basic.equals("WP")) {
                return WILD_PITCH;
            }
            break;
        case 'I':
            if (matchWithOptionalPlus(basic, "I") ||
                    matchWithOptionalPlus(basic, "IW")) {
                return WALK;
            }
            break;
        case 'E':
            return REACHED_ON_ERROR;
        case 'C':
            ImmutableList<String> modifiers = field.getModifiers();
            if (!modifiers.isEmpty() && modifiers.get(0).startsWith("E")) {
                return INTERFERENCE;
            }
            if (basic.length() >= 3) {
                char second = basic.charAt(1);
                char third = basic.charAt(2);
                if (second == 'S' && isStealableBase(third)) {
                    return CAUGHT_STEALING;
                }
            }
            break;
        case'F':
            if (matchWithOptionalFielderSuffix(basic, "FC")) {
                return FIELDERS_CHOICE;
            } else if (matchWithOptionalFielderSuffix(basic, "FLE")) {
                return ERROR_ON_FOUL_FLY;
            }
            break;
        case 'P':
            if (matchWithOptionalSuffix(basic, "PO", EventTypeParser::isOccupiableBase) ||
                    matchWithOptionalSuffix(basic, "POCS", EventTypeParser::isStealableBase)) {
                return PICKED_OFF;
            } else if (basic.equals("PB")) {
                return PASSED_BALL;
            }
        default:
            // Do nothing. More tests need to be performed below.
        }
        if (Character.isDigit(first)) {
            // This indicates an out of some sort. First check for some special cases.
            if (field.hasModifier(m -> m.startsWith("FO"))) {
                // We treat a force out as a fielder's choice.
                return FORCE_OUT;
            }
            return OUT;
        }
        switch (basic) {
        case "NP":
            return NO_PLAY;
        case "BK":
            return BALK;
        case "OA":
            return OTHER_ADVANCE;
        }
        throw new IllegalArgumentException("Invalid event field: " + field);
    }
    
    private static boolean matchWithOptionalFielderSuffix(String input, String match) {
        return matchWithOptionalSuffix(input, match, EventTypeParser::isValidFielder);
    }
    
    private static boolean matchWithOptionalPlus(String input, String match) {
        return matchWithOptionalSuffix(input, match, c -> c == '+');
    }
    
    private static boolean matchWithOptionalSuffix(String input, String match, 
            Predicate<Character> suffixCondition) {
        if (input.equals(match)) {
            return true;
        }
        if (input.length() <= match.length()) {
            return false;
        }
        if (!input.startsWith(match)) {
            return false;
        }
        char next = input.charAt(match.length());
        return suffixCondition.test(next) || next == '?';
    }
    
    private static boolean isValidFielder(char c) {
        return (c >= '1') && (c <= '9');
    }
    
    private static boolean isValidBase(char c) {
        return c =='1' || c == '2' || c == '3' || c == 'H';
    }
    
    private static boolean isStealableBase(char c) {
        return isValidBase(c) && c != '1'; // can't steal first base
    }
    
    private static boolean isOccupiableBase(char c) {
        return isValidBase(c) && c != 'H'; // can't occupy home
    }
    
    /**
     * Returns the basic play part of the field. This is the part that comes before 
     * any modifiers (separated by "/") or baserunning advance notations (separated by ".").
     */
    @VisibleForTesting
    static String getBasicPlay(String field) {
        String[] parts = field.split("\\/|\\.");
        String basic = parts[0];
        return basic.endsWith("#") || basic.endsWith("?")
                ? basic.substring(0, basic.length() - 1)
                : basic;
    }
    
    
    private EventTypeParser() {
        // no reason (yet) to create instances of this class
    }

}
