package bsbll.research;

import java.util.HashMap;
import java.util.Map;

import bsbll.research.Advance.Outcome;

/**
 * Parses the advance field part of the play field.
 */
public final class AdvanceFieldParser {
    /**
     * @param field
     *            the field
     * @param eventType
     *            implies the the base awarded to the batter, if not specified
     *            explicitly by the advance part, with the latter taking
     *            precedence. For example, on a Single the implied base awarded
     *            to the batter is FIRST, but the batter may end up advancing
     *            further e.g. due to errors on the play.
     */
    public static Advances parse(AdvanceField field, EventType eventType) {
        Map<Base, Advance> advances = new HashMap<>();
        for (String part : field.getParts()) {
            Advance a = fromString(part);
            advances.put(a.from(), a);
        }
        if (!advances.containsKey(Base.HOME)) {
            eventType.getImpliedBaseForBatter().ifPresent(to -> {
                advances.put(Base.HOME, new Advance(Base.HOME, to, Outcome.SAFE));
            });
        }
        return new Advances(advances.values());
    }
    
    private static Advance fromString(String s) {
        // Each part consists of P-Q or PXQ, followed by zero or
        // more expressions within parentheses. We're only interested
        // in the first part.
        String core = s.substring(0, 3);
        Base from = Base.fromChar(core.charAt(0));
        Outcome outcome = Outcome.fromChar(core.charAt(1));
        Base to = Base.fromChar(core.charAt(2));
        return new Advance(from, to, outcome);
    }
    
    private AdvanceFieldParser() {/**/}

}
