package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import bsbll.bases.OccupiedBases;
import bsbll.die.DieFactory;
import bsbll.game.play.EventType;

/**
 * The distribution of number of errors on plays of a given type, with a given base situation.
 */
@Immutable
public final class ErrorCountDistribution {
    private static final ErrorCountDistribution NO_ERRORS = new ErrorCountDistribution(ImmutableTable.of());
    
    // TODO: Should the number of outs be included as well?
    private final ImmutableTable<EventType, OccupiedBases, ImmutableMultiset<Integer>> data;
    private final DieFactory dieFactory = DieFactory.random();
    
    public ErrorCountDistribution(ImmutableTable<EventType, OccupiedBases, ImmutableMultiset<Integer>> data) {
        this.data = requireNonNull(data);
    }
    
    public static ErrorCountDistribution noErrors() {
        return NO_ERRORS;
    }

    public int getNumberOfErrors(EventType type, OccupiedBases baseSituation) {
        return getNumberOfErrors(type, baseSituation, this.dieFactory);
    }

    public int getNumberOfErrors(EventType type, OccupiedBases baseSituation, DieFactory dieFactory) {
        requireNonNull(type);
        requireNonNull(baseSituation);
        requireNonNull(dieFactory);
        ImmutableMultiset<Integer> values = data.get(type, baseSituation);
        if (values == null || values.isEmpty()) {
            return 0;
        }
        int total = values.size();
        int roll = dieFactory.newDie(total).roll();
        int sum = 0;
        for (Multiset.Entry<Integer> e : values.entrySet()) {
            sum += e.getCount();
            if (roll <= sum) {
                return e.getElement();
            }
        }
        // We should never get here. But in case we do...
        return 0;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final Table<EventType, OccupiedBases, Multiset<Integer>> data = HashBasedTable.create();
        
        public Builder add(EventType type, OccupiedBases bases, int errorCount) {
            requireNonNull(type);
            requireNonNull(bases);
            Multiset<Integer> values = data.get(type, bases);
            if (values == null) {
                values = HashMultiset.create();
                data.put(type, bases, values);
            }
            values.add(errorCount);
            return this;
        }
        
        public ErrorCountDistribution build() {
            ImmutableTable.Builder<EventType, OccupiedBases, ImmutableMultiset<Integer>> tableBuilder =
                    ImmutableTable.builder();
            for (Cell<EventType, OccupiedBases, Multiset<Integer>> c : data.cellSet()) {
                tableBuilder.put(c.getRowKey(), c.getColumnKey(), ImmutableMultiset.copyOf(c.getValue()));
            }
            return new ErrorCountDistribution(tableBuilder.build());
        }
    }
    
}
