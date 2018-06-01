package bsbll.stats;

import java.util.Comparator;

/**
 * Represents a single stat, such as Games Played, Hits, or Batting Average.
 *
 * @param <T> the type of value associated with the stat, such as Integer, or Average.
 */
public interface Stat<T> {
    String abbrev();
    
    /**
     * Returns a Comparator for sorting this stats, so that the resulting order
     * is the order in which the leaders of this stat is sorted. (Ugh. Rewrite
     * the previous sentence.)
     * <p>
     * For a pitching example: Both ERA and SO/9 is represented by the same
     * value class, {@link Per9IPStat}. For ERA, a lower value is better, but
     * for SO/9 a higher value is better.
     */
    Comparator<T> leaderOrder();
    
    // TODO: I would like a generic declaration of the get(StatLine) method here, but
    // I don't know yet how to pull that off. The generic trickery becomes rather
    // immense, unless I'm missing something obvious. The problem is a circular
    // type dependency: Stat would have a generic type referring to StatLine, 
    // which has a generic type referring back to Stat.
    // Maybe what's suggested in this post will work:
    //    https://stackoverflow.com/questions/3147308/cyclic-generic-dependenices
    
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
