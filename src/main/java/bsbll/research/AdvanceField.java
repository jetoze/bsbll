package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import bsbll.Base;

final class AdvanceField {
    private final ImmutableSortedMap<Base, String> parts;
    
    private AdvanceField() {
        this.parts = ImmutableSortedMap.of();
    }
    
    public AdvanceField(Map<Base, String> parts) {
        this.parts = ImmutableSortedMap.<Base, String>orderedBy(Base.comparingOrigin())
                .putAll(parts)
                .build();
    }
    
    public boolean isEmpty() {
        return this.parts.isEmpty();
    }
    
    public ImmutableCollection<String> getParts() {
        return this.parts.values();
    }
    
    public boolean isError(Base from) {
        return countErrors(from) > 0;
    }
    
    public int countErrors(Base from) {
        requireNonNull(from);
        String s = this.parts.get(from);
        return (s != null)
                ? ParseUtils.countErrorIndicators(s)
                : 0;
    }
    
    public int countAllErrors() {
        return this.parts.keySet().stream()
                .mapToInt(this::countErrors)
                .sum();
    }
 
    @Override
    public String toString() {
        return isEmpty()
                ? "[empty]"
                : getParts().stream().collect(joining(";"));
    }
    
    public static AdvanceField fromString(String s) {
        if (s.isEmpty()) {
            return new AdvanceField();
        }
        Map<Base, String> parts = new HashMap<>();
        for (String p : s.split(";")) {
            String tp = p.trim();
            if (tp.isEmpty()) {
                continue;
            }
            checkArgument(tp.length() >= 3, "Invalid advance field: %s", s);
            Base from = Base.fromChar(tp.charAt(0));
            parts.put(from, p);
        }
        return new AdvanceField(parts);
    }
    
    static ImmutableList<Annotation> extractAnnotations(String s) {
        ImmutableList.Builder<Annotation> builder = ImmutableList.builder();
        int start = s.indexOf('(');
        while (start != -1) {
            int end = s.indexOf(')', start + 1);
            if (end == -1) {
                break;
            }
            String x = s.substring(start + 1, end);
            Annotation a = Annotation.fromString(x);
            builder.add(a);
            start = s.indexOf('(', end + 1);
        }
        return builder.build();
    }
    
    
    /**
     * The different tpyes of info that can be associated with an individual advance. 
     */
    @VisibleForTesting
    static enum Annotation {
        /**
         * The fielders that participated in the put out if the runner is out. (Note that
         * we don't provide access to the individual fielder's that were involved.) 
         */
        FIELDERS,
        /**
         * There was an error on the play. If this info type is present, an
         * indicated out is negated and turned into a safe-on-error, unless
         * there is also an associated {@link FIELDERS} annotation. The latter
         * case means the runner was allowed to take extra bases because of an
         * error, but was later thrown out anyway.
         */
        ERROR,
        /**
         * For a run, indicates that the run is unearned.
         */
        UNEARNED_RUN,
        /**
         * For a run, indicates that the batter is not credited with an RBI.
         */
        NO_RBI;
        
        public static Annotation fromString(String s) {
            checkArgument(s.length() >= 1);
            if (s.contains("E")) {
                return ERROR;
            }
            char first = s.charAt(0);
            if (Character.isDigit(first)) {
                return FIELDERS;
            }
            if (s.equals("UR")) {
                return UNEARNED_RUN;
            }
            if (s.equals("NR")) {
                return NO_RBI;
            }
            throw new IllegalArgumentException("Unrecognized annotation: " + s);
        }
    }
    
}
