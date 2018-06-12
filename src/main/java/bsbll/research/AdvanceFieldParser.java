package bsbll.research;

import static java.util.stream.Collectors.toMap;

import java.util.Map;

import bsbll.bases.Advance;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.research.AdvanceField.Part;

/**
 * Parses the advance field part of the play field.
 */
public final class AdvanceFieldParser {
    // TODO: This class is mostly superfluous now that all interesting parsing is done
    // directly in the AdvanceField class itself.
    
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
        Map<Base, Advance> advances = field.getParts().stream()
                .collect(toMap(Part::from, Part::getAdvance));
        if (!advances.containsKey(Base.HOME)) {
            eventType.getImpliedAdvance().ifPresent(a -> advances.put(a.from(), a));
        }
        return new Advances(advances.values());
    }
    
    private AdvanceFieldParser() {/**/}

}
