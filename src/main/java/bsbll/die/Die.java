package bsbll.die;

/**
 * Represents a die with some number of sides.
 * <p>
 * Implementations must be thread safe.
 */
@FunctionalInterface
public interface Die {
    /**
     * Rolls the die and returns the result.
     * 
     * @return a value in the range 1 (inclusive) and N (inclusive), where N is
     *         the number of sides.
     */
    int roll();

}
