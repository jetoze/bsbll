package bsbll.stats;

/**
 * Represents a single stat, such as Games Played, Hits, or Batting Average.
 *
 * @param <T> the type of value associated with the stat, such as Integer, or Average.
 */
public interface Stat<T> {
    // TODO: Add this
    // String abbrev();
    
    /**
     * The {@code PrimitiveStat}s are the individual stats that are actually
     * counted. Other stats can then be composed from two or more primitive
     * stats.
     * <p>
     * An example from pitching: the number of outs a pitcher has made, and the
     * number of earned runs he has allowed, are primitive stats. The pitcher's
     * ERA is a composed stat, that can be derived from the former two.
     */
    public static interface PrimitiveStat extends Stat<Integer> {
        /**/
    }

}
