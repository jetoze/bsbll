package bsbll.research.pbpf;

/**
 * Signals a play-by-play file parser that it should stop parsing.
 */
public final class StopParsingException extends RuntimeException {
    public StopParsingException() {
        super();
    }
}
