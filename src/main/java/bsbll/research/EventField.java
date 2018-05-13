package bsbll.research;

import static java.util.stream.Collectors.joining;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.List;
import java.util.Objects;

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

    public EventField(String basicPlay, List<String> modifiers, String advanceField) {
        this.basicPlay = checkNotEmpty(basicPlay);
        this.modifiers = ImmutableList.copyOf(modifiers);
        this.advanceField = Strings.nullToEmpty(advanceField);
    }

    public static EventField fromString(String s) {
        checkNotEmpty(s);
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
    }

    public String getBasicPlay() {
        return basicPlay;
    }

    public ImmutableList<String> getModifiers() {
        return modifiers;
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
        String mod = this.modifiers.stream()
                .collect(joining("/", "/", ""));
        sb.append(mod);
        if (!advanceField.isEmpty()) {
            sb.append(".").append(advanceField);
        }
        return sb.toString();
    }
}
