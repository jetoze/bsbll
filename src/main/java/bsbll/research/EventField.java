package bsbll.research;

import static java.util.stream.Collectors.joining;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

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
    private final String basicPlay;
    private final ImmutableList<String> modifiers;
    private final String advanceField;

    public EventField(String basicPlay, Collection<String> modifiers, String advanceField) {
        this.basicPlay = checkNotEmpty(basicPlay);
        this.modifiers = ImmutableList.copyOf(modifiers);
        this.advanceField = Strings.nullToEmpty(advanceField);
    }

    public static EventField fromString(String input) {
        checkNotEmpty(input);
        try {
            String s = (input.endsWith("#") || input.endsWith("?"))
                    ? input.substring(0, input.length() - 1)
                    : input;
            int indexOfFirstModSep = s.indexOf('/');
            int indexOfAdvSep = s.indexOf('.');
            if (indexOfFirstModSep == -1 && indexOfAdvSep == -1) {
                return new EventField(s, ImmutableList.of(), "");
            }
            String basic = (indexOfFirstModSep == -1)
                    ? s.substring(0, indexOfAdvSep)
                    : s.substring(0, indexOfFirstModSep);
            String advance = (indexOfAdvSep == -1) 
                    ? ""
                    : s.substring(indexOfAdvSep + 1);
            String modifiersPart = (indexOfFirstModSep == -1)
                    ? ""
                    : (indexOfAdvSep == -1)
                        ? s.substring(indexOfFirstModSep + 1)
                        : s.substring(indexOfFirstModSep + 1, indexOfAdvSep);
            ImmutableList<String> modifiers = modifiersPart.isEmpty()
                    ? ImmutableList.of()
                    : ImmutableList.copyOf(modifiersPart.split("\\/"));
            return new EventField(basic, modifiers, advance);
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

    public String getAdvanceField() {
        return advanceField;
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
