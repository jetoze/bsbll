package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Parses the advance field part of the play field.
 */
public final class AdvanceFieldParser {
    /**
     * @param field
     *            the field, or an empty string if the play field does not
     *            contain an advance part.
     * @param eventType
     *            implies the the base awarded to the batter, if not specified
     *            explicitly by the advance part, with the latter taking
     *            precedence. For example, on a Single the implied base awarded
     *            to the batter is FIRST, but the batter may end up advancing
     *            further e.g. due to errors on the play.
     */
    public static Result parse(String field, EventType eventType) {
        Map<Base, Base> advances = new HashMap<>();
        Set<Base> outs = new HashSet<>();
        String[] parts = field.split(";");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) {
                continue;
            }
            checkArgument(trimmedPart.length() >= 3, "Invalid advance field: %s", field);
            // Each part consists of P-Q or PXQ, followed by zero or
            // more expressions within parentheses. We're only interested
            // in the first part.
            String core = trimmedPart.substring(0, 3);
            Base from = Base.fromChar(core.charAt(0));
            switch (core.charAt(1)) {
            case '-':
                Base to = Base.fromChar(core.charAt(2));
                if (from != to || from == Base.HOME) {
                    advances.put(from, to);
                }
                break;
            case 'X':
                outs.add(from);
                break;
            default:
                throw new IllegalArgumentException("Invalid advance field: " + field);
            }
        }
        if (!outs.contains(Base.HOME) && !advances.containsKey(Base.HOME)) {
            eventType.getImpliedBaseForBatter().ifPresent(b -> advances.put(Base.HOME, b));
        }
        sanityCheck(field, advances, outs);
        return new Result(
                new Advances(advances), 
                outs);
    }

    private static void sanityCheck(String field, Map<Base, Base> advances, Set<Base> outs) {
        Set<Base> check = new HashSet<>(advances.keySet());
        check.retainAll(outs);
        checkArgument(check.isEmpty(), "Invalid advance field: %s. The following bases are indicated to both advance and have been out: %s",
                field, check);
    }
    
    
    public static final class Result {
        private final Advances advances;
        private final ImmutableSet<Base> outs;
        
        public Result(Advances advances, Set<Base> outs) {
            this.advances = requireNonNull(advances);
            this.outs = ImmutableSet.copyOf(outs);
            checkArgument(outs.stream().noneMatch(advances::didRunnerAdvance));
        }

        /**
         * Returns the advances made by the base runners (including the batter).
         */
        public Advances getAdvances() {
            return advances;
        }

        /**
         * Represents the base runners that were out as a result of the play
         * (including the batter).
         * 
         * @return a set of the bases where the players that were out were on at
         *         the start of the play.
         */
        public ImmutableSet<Base> getOuts() {
            return outs;
        }
    }
    
    private AdvanceFieldParser() {/**/}

}
