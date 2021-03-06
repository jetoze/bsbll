package bsbll.research;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsbll.bases.Advance;
import bsbll.bases.Advance.Outcome;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;

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
            EventParser parser = new EventParser(field);
            return parser.parse();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid field: %s. Reported error: %s", 
                    field, e.getMessage()), e);
        }
    }
    
    private final EventField field;
    private Advances advances;
    private int numberOfErrors;
    
    private EventParser(EventField field) {
        this.field = requireNonNull(field);
    }

    private PlayOutcome parse() {
        EventType eventType = EventTypeParser.parse(field);
        this.advances = AdvanceFieldParser.parse(field.getAdvanceField(), eventType);
        
        // Here starts the special cases...
        switch (eventType) {
        case REACHED_ON_ERROR:
            handleReachedOnError();
            break;
        case STOLEN_BASE:
            handleStolenBase();
            break;
        case CAUGHT_STEALING:
            handleCaughtStealing();
            break;
        case PICKED_OFF:
            handlePickoff();
            break;
        case STRIKEOUT:
            handleAdditionalEvent();
            break;
        case WALK:
            handleAdditionalEvent();
            break;
        case OUT:
            handleSpecialCasesOnOut();
            break;
        case FORCE_OUT:
            handleForceOut();
            break;
        default:
            // no additional processing needed
        }
        
        if (eventType.isError()) {
            ++numberOfErrors;
        }
        numberOfErrors += field.getAdvanceField().countAllErrors();
        
        return new PlayOutcome(
                eventType, 
                advances, 
                numberOfErrors);
    }
    
    private void handleReachedOnError() {
        if (this.advances.contains(Base.HOME)) {
            Advance a = this.advances.getAdvanceFrom(Base.HOME);
            if (a.isSafe()) {
                replaceAdvance(Advance.safeOnError(Base.HOME, a.to()));
            }
        }
    }
    
    private void handleStolenBase() {
        handleStolenBase(field.getBasicPlay());
    }
    
    private void handleStolenBase(String marker) {
        String[] parts = marker.split(";");
        for (String p : parts) {
            if (p.startsWith("SB")) {
                Base stolen = Base.fromChar(p.charAt(2));
                Base from = stolen.preceding();
                if (this.advances.contains(from)) {
                    lookForErrorOnStolenBase(stolen, from);
                } else {
                    addAdvance(Advance.safe(from, stolen));
                }
            }
        }
    }

    public void lookForErrorOnStolenBase(Base stolen, Base from) {
        Advance a = this.advances.getAdvanceFrom(from);
        if (a.to().compareTo(stolen) > 0) {
            // Indicates an error was made, allowing the runner to take additional bases.
            // Increase the error count, unless the error is already given explicitly
            // in the advance field itself (they are counted separately)
            if (!field.getAdvanceField().isError(from)) {
                ++numberOfErrors;
            }
        }
    }

    private void handleCaughtStealing() {
        handleCaughtStealing(field.getBasicPlay());
    }

    private void handleCaughtStealing(String marker) {
        assert marker.startsWith("CS");
        Base caughtAt = Base.fromChar(marker.charAt(2));
        Base ranFrom = caughtAt.preceding();
        if (advances.contains(ranFrom)) {
            if (advances.didRunnerAdvance(ranFrom)) {
                // This is the case where an error negates the CS.
                ++numberOfErrors;
            }
        } else {
            Outcome outcome = Outcome.OUT;
            if (isErrorOnCaughtStealing(marker)) {
                outcome = Outcome.SAFE_ON_ERROR;
                ++numberOfErrors;
            }
            addAdvance(new Advance(ranFrom, caughtAt, outcome));
        }
        // Was more than one runner caught stealing?
        int next = marker.indexOf(";CS");
        if (next > 0) {
            String nextMarker = marker.substring(next + 1);
            handleCaughtStealing(nextMarker);
        }
    }
    
    private static boolean isErrorOnCaughtStealing(String basicPlay) {
        assert basicPlay.startsWith("CS");
        return basicPlay.matches("CS[23H].*\\(\\d*E\\d+\\).*");
    }

    private void handlePickoff() {
        handlePickoff(this.field.getBasicPlay());
    }
    
    private void handlePickoff(String marker) {
        assert marker.startsWith("PO");
        int errors = ParseUtils.countErrorIndicators(marker);
        if (errors > 0) {
            this.numberOfErrors += errors;
        }
        if (marker.startsWith("POCS")) {
            Base to = Base.fromChar(marker.charAt(4));
            Base from = to.preceding();
            Outcome outcome = (errors == 0)
                    ? Outcome.OUT
                    : Outcome.SAFE_ON_ERROR;
            addAdvanceIfNotPresent(new Advance(from, to, outcome));
        } else {
            if (errors > 0) {
                // The error negates the pickoff, so nothing to do.
                return;
            }
            Base from = Base.fromChar(marker.charAt(2));
            Base to = from;
            addAdvanceIfNotPresent(Advance.out(from, to));
        }
    }
    
    private void handleSpecialCasesOnOut() {
        if (isDoublePlay()) {
            handleDoublePlay();
        } else if (isTriplePlay()) {
            handleTriplePlay();
        } else {
            Base from = getBaseOfRunnerThatWasThrownOut();
            if (from == Base.HOME) {
                // This is the normal case of the batter being thrown out.
                return;
            }
            Base outAt = from.next(); // implied
            addAdvanceIfNotPresent(Advance.out(from, outAt));
            // The batter is awarded first. In case we've already recorded that
            // the batter is out, correct that.
            replaceAdvance(Advance.safe(Base.HOME, Base.FIRST));
        }
    }
    
    private boolean isDoublePlay() {
        return this.field.getModifiers().stream()
                .anyMatch(s -> s.startsWith("GDP") || s.startsWith("FDP") || 
                        s.startsWith("LDP") || s.startsWith("DP") ||
                        s.startsWith("BPDP") ||
                        // NDP -> "no double play credited for this play". Never the less, two outs were made.
                        s.startsWith("NDP"));
    }

    private void handleDoublePlay() {
        Set<Base> indicatedOuts = getIndicatedOutsFromBasicPlay(this.field.getBasicPlay());
        recordOutsAtNextBase(indicatedOuts);
        this.advances.stream()
            .filter(Advance::isOut)
            .map(Advance::from)
            .forEach(indicatedOuts::add);
        if (!advances.contains(Base.HOME)) {
            // The batter's fate was not given explicitly in the advance section.
            // Let's see what happened to him.
            if (indicatedOuts.size() == 2 && !indicatedOuts.contains(Base.HOME)) {
                // Both outs were on the bases --> The batter is implicitly safe at first.
                addAdvance(Advance.safe(Base.HOME, Base.FIRST));
            } else if (indicatedOuts.size() == 1) {
                // Only one base runner was indicated as out --> The batter is implicitly out as well.
                addOut(Base.HOME, Base.FIRST);
            }
        }
    }
    
    private boolean isTriplePlay() {
        return this.field.getModifiers().stream()
                .anyMatch(s -> s.startsWith("LTP") || s.startsWith("GTP") || s.startsWith("TP"));
    }

    private void handleTriplePlay() {
        processOutsIndicatedInBasicPlay(this.field.getBasicPlay());
    }
    
    private static final Pattern OUT_AT_BASE_PATTERN = Pattern.compile("\\d+\\((\\d)\\).*");
    
    private Base getBaseOfRunnerThatWasThrownOut() {
        String basicPlay = this.field.getBasicPlay();
        Matcher matcher = OUT_AT_BASE_PATTERN.matcher(basicPlay);
        if (matcher.matches()) {
            String s = matcher.group(1);
            return Base.fromChar(s.charAt(0));
        }
        return Base.HOME;
    }
    
    private void handleForceOut() {
        // The batter is expected to reach first on a force out.
        EnumSet<Base> indicatedOuts = getIndicatedOutsFromBasicPlay(this.field.getBasicPlay());
        assert !indicatedOuts.isEmpty();
        recordOutsAtNextBase(indicatedOuts);
        // A force out at third implies that there was a runner on first, and this
        // runner is now safe at second, etc. We must handle this here, since retrosheet 
        // often doesn't give the advances explicitly. For example, "12(3)/FO"
        List<Base> impliedAdvances = new ArrayList<>();
        if (indicatedOuts.contains(Base.THIRD)) {
            impliedAdvances.add(Base.SECOND);
            impliedAdvances.add(Base.FIRST);
        } else if (indicatedOuts.contains(Base.SECOND)) {
            impliedAdvances.add(Base.FIRST);
        }
        // The batter is always assumed to advance, and this is handled through
        // EventType.FORCE_OUT.getImpliedAdvance().
        impliedAdvances.stream()
            .filter(b -> !advances.contains(b))
            .map(b -> Advance.safe(b, b.next()))
            .forEach(this::addAdvance);
    }
    
    /**
     * 54(1) -> Base.FIRST out at SECOND/
     * 3(B)6(1) -> Base.HOME (batter) out at FIRST, Base.FIRST out at SECOND
     */
    private void processOutsIndicatedInBasicPlay(String basicPlay) {
        EnumSet<Base> indicatedOuts = getIndicatedOutsFromBasicPlay(this.field.getBasicPlay());
        recordOutsAtNextBase(indicatedOuts);

    }

    public void recordOutsAtNextBase(Collection<Base> bases) {
        bases.stream()
            .filter(advances::isNotKnown)
            .forEach(b -> addOut(b, b.next()));
    }

    /**
     * 54(1) -> Base.FIRST
     * 3(B)6(1) -> Base.HOME (batter), Base.FIRST
     */
    private static EnumSet<Base> getIndicatedOutsFromBasicPlay(String basicPlay) {
        EnumSet<Base> bases = EnumSet.noneOf(Base.class);
        int start = basicPlay.indexOf('(');
        while (start != -1) {
            int end = basicPlay.indexOf(')', start + 1);
            if (end == -1) {
                break;
            }
            if (end - start == 2) {
                bases.add(Base.fromChar(basicPlay.charAt(start + 1)));
            }
            start = basicPlay.indexOf('(', end + 1);
        }
        return bases;
    }
    
    private void handleAdditionalEvent() {
        int sep = this.field.getBasicPlay().indexOf('+');
        if (sep == -1) {
            return;
        }
        String additionalEventString = this.field.getBasicPlay().substring(sep + 1);
        EventType additionalEventType = EventTypeParser.parse(additionalEventString);
        switch (additionalEventType) {
        case STOLEN_BASE:
            handleStolenBase(additionalEventString);
            break;
        case CAUGHT_STEALING:
            handleCaughtStealing(additionalEventString);
            break;
        case PICKED_OFF:
            handlePickoff(additionalEventString);
            break;
        case WILD_PITCH:
            // Nothing to do(?). All relevant side effects are handled via the 
            // advance field.
            break;
        case PASSED_BALL:
            // Same as WILD_PITCH
            break;
        case REACHED_ON_ERROR:
            // TODO: Implement me.
            break;
        case OTHER_ADVANCE:
            // TODO: Implement me
            break;
        case DEFENSIVE_INDIFFERENCE:
            // TODO: Implement me
            break;
        default:
            throw new RuntimeException("Unexpected additional event type: " + additionalEventType + " (" + this.field + ")");
        }
    }
    
    private void addOut(Base from, Base to) {
        addAdvance(Advance.out(from, to));
    }

    private void addAdvance(Advance a) {
        checkState(this.advances != null);
        this.advances = this.advances.concat(a);
    }
    
    private void addAdvanceIfNotPresent(Advance a) {
        checkState(this.advances != null);
        if (!this.advances.contains(a.from())) {
            this.advances = this.advances.concat(a);
        }
    }
    
    private void replaceAdvance(Advance a) {
        checkState(this.advances != null);
        if (this.advances.contains(a.from())) {
            this.advances = this.advances.replace(a);
        } else {
            addAdvance(a);
        }
    }
}
