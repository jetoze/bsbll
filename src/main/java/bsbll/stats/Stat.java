package bsbll.stats;

/**
 * Represents a single stat, such as Games Played, Hits, or Batting Average.
 *
 * @param <T> the type of value associated with the stat, such as Integer, or Average.
 */
public interface Stat<T> {
    // TODO: Add this
    // String abbrev();
    
    public static interface PrimitiveStat<U extends PrimitiveStat<U>> extends Stat<Integer> {
        /**/
    }

}
