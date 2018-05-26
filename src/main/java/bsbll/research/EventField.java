package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Represents the event field in a play record from a retrosheet play-by-play
 * file. The field has been broken down into its individual components: a basic
 * play description, an optional list of modifiers, and an optional advance
 * field.
 */
public final class EventField {
    private static final boolean USE_REGEX_PARSING = true;
    private static final Pattern REGEX_PATTERN = compileRegexPattern();
    
    private static Pattern compileRegexPattern() {
        // The basic play part consists of one or more characters not including
        // "/" or ".", plus an optional sequence of additional groups within
        // parentheses. (These additional groups can contain "/".)
        String basicPlayMain = "(?:[^/\\.\\(\\)]+)";
        String basicPlayAnnotation = "(?:\\([^\\(\\)]+\\))*";
        String basicPlay = "((?:" + basicPlayMain + basicPlayAnnotation + ")+)";
        
        // Repeated group of "/" followed by one or more characters that are not "/"
        // Note that we are capturing a repeated group, rather than repeating a captured group
        // (which is a common mistake). The inner group, which ends up matching the last 
        // modifier, is not of interest, so we mark it as a non-capturing group (?:).
        String modifiers = "((?:/[^/\\.]+)*)";
        
        // "." followed by one or more characters.
        String advance = "(\\.(.+))?";
        
        // Lastly, the field may end with some special characters (/, #, ?) that we 
        // can ignore.
        String regex = "(?:" + basicPlay + modifiers + advance + ")[/#\\?]*";
        return Pattern.compile(regex);
    }
    
    private final String basicPlay;
    private final ImmutableList<String> modifiers;
    private final AdvanceField advanceField;
    private final String rawString;

    public EventField(String basicPlay, Collection<String> modifiers, String advanceField, String rawString) {
        this.basicPlay = checkNotEmpty(basicPlay);
        this.modifiers = ImmutableList.copyOf(modifiers);
        this.advanceField = AdvanceField.fromString(Strings.nullToEmpty(advanceField));
        this.rawString = checkNotEmpty(rawString);
    }
    
    public static EventField fromString(String input) {
        checkNotEmpty(input);
        return USE_REGEX_PARSING
                ? parseWithRegex(input)
                : parseManually(input);
    }
    
    private static EventField parseWithRegex(String input) {
        Matcher matcher = REGEX_PATTERN.matcher(input);
        checkArgument(matcher.matches(), "Invalid event field: %s", input);
        String basicPlay = matcher.group(1);
        String modifiersPart = matcher.group(2);
        ImmutableList<String> modifiers = Strings.isNullOrEmpty(modifiersPart)
                ? ImmutableList.of()
                : ImmutableList.copyOf(modifiersPart.substring(1).split("/")); // Remove the first "/", to avoid an empty element
        String advance = Strings.nullToEmpty(matcher.group(4));
        return new EventField(basicPlay, modifiers, advance, input);
    }

    private static EventField parseManually(String input) {
        // FIXME: This version does not handle a field like the following:
        //            PO1(E2/TH).2-3
        //        --> There can be slashes in the basic part.
        // FIXME: This version also doesn't handle trailing [/,#,?].
        try {
            int indexOfAdvSep = input.indexOf('.');
            int indexOfFirstModSep = input.indexOf('/');
            if (indexOfFirstModSep == -1 && indexOfAdvSep == -1) {
                return new EventField(input, ImmutableList.of(), "", input);
            }
            if ((indexOfAdvSep > 0) && (indexOfFirstModSep > indexOfAdvSep)) {
                // There are no main modifiers, but the advance field contains a modifier with a slash.
                indexOfFirstModSep = -1;
            }
            String basic = (indexOfFirstModSep == -1)
                    ? input.substring(0, indexOfAdvSep)
                    : input.substring(0, indexOfFirstModSep);
            String advance = (indexOfAdvSep == -1) 
                    ? ""
                    : input.substring(indexOfAdvSep + 1);
            String modifiersPart = (indexOfFirstModSep == -1)
                    ? ""
                    : (indexOfAdvSep == -1)
                        ? input.substring(indexOfFirstModSep + 1)
                        : input.substring(indexOfFirstModSep + 1, indexOfAdvSep);
            ImmutableList<String> modifiers = modifiersPart.isEmpty()
                    ? ImmutableList.of()
                    : ImmutableList.copyOf(modifiersPart.split("/"));
            return new EventField(basic, modifiers, advance, input);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(String.format("Invalid event field: %s. Reported error: %s",
                    input, e.getMessage()), e);
        }
    }

    public String getBasicPlay() {
        return basicPlay;
    }

    public ImmutableList<String> getModifiers() {
        return modifiers;
    }
    
    public boolean hasModifier(Predicate<? super String> condition) {
        return modifiers.stream().anyMatch(condition);
    }

    public AdvanceField getAdvanceField() {
        return advanceField;
    }
    
    public String getRawString() {
        return rawString;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.basicPlay, this.modifiers, this.advanceField);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EventField) {
            EventField that = (EventField) obj;
            return this.basicPlay.equals(that.basicPlay)
                    && this.modifiers.equals(that.modifiers)
                    && this.advanceField.equals(that.advanceField);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.basicPlay);
        if (!this.modifiers.isEmpty()) {
            String mod = this.modifiers.stream().collect(joining("/", "/", ""));
            sb.append(mod);
        }
        if (!advanceField.isEmpty()) {
            sb.append(".").append(advanceField);
        }
        return sb.toString();
    }
}
