package bsbll.game;

public final class InvalidBaseSitutationException extends RuntimeException {
    public InvalidBaseSitutationException(String message) {
        super(message);
    }

    public InvalidBaseSitutationException(Throwable cause) {
        super(cause);
    }

    public InvalidBaseSitutationException(String message, Throwable cause) {
        super(message, cause);
    }
}
