package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import bsbll.Base;
import bsbll.research.Advance.Outcome;

final class AdvanceField {
    private final ImmutableSortedMap<Base, Part> parts;
    
    private AdvanceField() {
        this.parts = ImmutableSortedMap.of();
    }
    
    public AdvanceField(Stream<Part> parts) {
        Map<Base, Part> tmp = parts.collect(Collectors.toMap(Part::from, p -> p));
        this.parts = ImmutableSortedMap.<Base, Part>orderedBy(Base.comparingOrigin())
                .putAll(tmp)
                .build();
    }
    
    public boolean isEmpty() {
        return this.parts.isEmpty();
    }
    
    public ImmutableCollection<Part> getParts() {
        return this.parts.values();
    }
    
    public boolean isError(Base from) {
        return countErrors(from) > 0;
    }
    
    public int countErrors(Base from) {
        requireNonNull(from);
        Part p = this.parts.get(from);
        return (p != null)
                ? p.countErrors()
                : 0;
    }
    
    public int countAllErrors() {
        return this.parts.values().stream()
                .mapToInt(Part::countErrors)
                .sum();
    }
 
    @Override
    public String toString() {
        return isEmpty()
                ? "[empty]"
                : getParts().stream().map(Part::toString).collect(joining(";"));
    }
    
    public static AdvanceField fromString(String s) {
        if (s.isEmpty()) {
            return new AdvanceField();
        }
        Stream<Part> stream = Arrays.stream(s.split(";"))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .map(Part::fromString);
        return new AdvanceField(stream);
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
    
    public static final class Part {
        private final String raw;
        private final Advance advance;
        private final ImmutableList<Annotation> annotations;
        
        public Part(Advance advance, ImmutableList<Annotation> annotations, String raw) {
            this.advance = requireNonNull(advance);
            this.annotations = requireNonNull(annotations);
            this.raw = requireNonNull(raw);
        }
        
        public static Part fromString(String s) {
            checkArgument(s.length() >= 3, "Invalid advance part: " + s);
            Base from = Base.fromChar(s.charAt(0));
            Base to = Base.fromChar(s.charAt(2));
            Outcome outcome = Outcome.fromChar(s.charAt(1));
            ImmutableList<Annotation> annotations = extractAnnotations(s);
            if (outcome == Outcome.OUT) {
                // Check if the runner is safe on an error.
                if (annotations.contains(Annotation.ERROR) && !annotations.contains(Annotation.FIELDERS)) {
                    outcome = Outcome.SAFE_ON_ERROR;
                }
            }
            Advance a = new Advance(from, to, outcome);
            return new Part(a, annotations, s);
        }

        public Advance getAdvance() {
            return advance;
        }
        
        public Base from() {
            return advance.from();
        }

        public ImmutableList<Annotation> getAnnotations() {
            return annotations;
        }
        
        public int countErrors() {
            return (int) annotations.stream()
                    .filter(a -> a == Annotation.ERROR)
                    .count();
        }
        
        @Override
        public String toString() {
            return raw;
        }
    }
    
    
    /**
     * The different types of info that can be associated with an individual advance. 
     */
    public static enum Annotation {
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
         * For a run, explicitly indicates that a the batter is credited with an
         * RBI.
         */
        RBI,
        /**
         * For a run, indicates that the run is unearned.
         */
        UNEARNED_RUN,
        /**
         * For a run, indicates that the batter is not credited with an RBI.
         */
        NO_RBI,
        /**
         * Advance due to passed ball.
         */
        PASSED_BALL,
        /**
         * Advance due to wild pitch.
         */
        WILD_PITCH;
        /**
         * Any other annotations (e.g. PB (passed ball), WP (wild pitch)).
         */
        //OTHER;
        
        public static Annotation fromString(String s) {
            checkArgument(s.length() >= 1);
            if (s.contains("E")) {
                return ERROR;
            }
            char first = s.charAt(0);
            if (Character.isDigit(first) || s.startsWith("TH")) {
                return FIELDERS;
            }
            switch (s) {
            case "UR":
            case "TUR":
                return UNEARNED_RUN;
            case "NR":
                return NO_RBI;
            case "RBI":
                return RBI;
            case "PB":
                return PASSED_BALL;
            case "WP":
                return WILD_PITCH;
            default:
                throw new IllegalArgumentException("Unrecognized annotation: " + s);
            }
        }
    }
    
}
