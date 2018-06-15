package bsbll.game.params;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import bsbll.game.play.EventType;

public final class ErrorSupport {
    /**
     * The type of events that are subject to errors in the current implementation.
     */
    public static final ImmutableSet<EventType> SUPPORTED_TYPES = Sets.immutableEnumSet(
            EventType.OUT, EventType.SINGLE, EventType.DOUBLE, EventType.TRIPLE);

    public static boolean isSupported(EventType type) {
        requireNonNull(type);
        return SUPPORTED_TYPES.contains(type);
    }
    
    public static EventType requireSupported(EventType type) {
        checkArgument(isSupported(type), "Unsupported type: %s");
        return type;
    }
    
    private ErrorSupport() {/**/}
}
