package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import bsbll.die.DieFactory;
import bsbll.game.play.EventType;
import p3.Persister;

/**
 * The distribution of number of errors on plays of a given type, with a given base situation.
 */
@Immutable
public final class ErrorCountDistribution {
    private static final ErrorCountDistribution NO_ERRORS = new ErrorCountDistribution(ImmutableTable.of());
    
    // TODO: The error count distribution should depend on the team, to simulate some teams
    // being better than fielding than others. As it stands, we expect all teams to end up
    // with approximately the same number of errors over the course of a season.
    
    // TODO: Should the number of outs be included as well?
    private final ImmutableTable<EventType, OccupiedBases, ImmutableMultiset<Integer>> data;
    
    public ErrorCountDistribution(ImmutableTable<EventType, OccupiedBases, ImmutableMultiset<Integer>> data) {
        this.data = requireNonNull(data);
    }
    
    public static ErrorCountDistribution noErrors() {
        return NO_ERRORS;
    }

    public int getNumberOfErrors(EventType type, BaseSituation baseSituation, DieFactory dieFactory) {
        requireNonNull(type);
        requireNonNull(baseSituation);
        requireNonNull(dieFactory);
        ImmutableMultiset<Integer> values = data.get(type, baseSituation.getOccupiedBases());
        if (values == null || values.isEmpty()) {
            return 0;
        }
        int total = values.size();
        int roll = dieFactory.getDie(total).roll();
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
    
    public void store(Persister p) {
        for (EventType et : data.rowKeySet()) {
            Persister typePersister = p.newChild("EventType");
            typePersister.putString("Type", et.name());
            ImmutableMap<OccupiedBases, ImmutableMultiset<Integer>> row = data.row(et);
            for (Map.Entry<OccupiedBases, ImmutableMultiset<Integer>> er : row.entrySet()) {
                Persister entryPersister = typePersister.newChild("Entry");
                entryPersister.putString("Bases", er.getKey().name());
                er.getValue().entrySet().forEach(e -> {
                    Persister valuePersister = entryPersister.newChild("Values");
                    valuePersister.putInt("Errors", e.getElement()).putInt("Count", e.getCount());
                });
            }
        }
    }
    
    public static ErrorCountDistribution restoreFrom(Persister p) {
        Builder builder = builder();
        for (Persister typePersister : p.getChildren("EventType")) {
            EventType type = EventType.valueOf(typePersister.getString("Type"));
            for (Persister entryPersister : typePersister.getChildren("Entry")) {
                OccupiedBases bases = OccupiedBases.valueOf(entryPersister.getString("Bases"));
                for (Persister valuePersister : entryPersister.getChildren("Values")) {
                    int errorCount = valuePersister.getInt("Errors");
                    int occurrences = valuePersister.getInt("Count");
                    builder.setCount(type, bases, errorCount, occurrences);
                }
            }
        }
        return builder.build();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof ErrorCountDistribution) && this.data.equals(((ErrorCountDistribution) obj).data);
    }
    
    @Override
    public int hashCode() {
        return data.hashCode();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final Table<EventType, OccupiedBases, Multiset<Integer>> data = HashBasedTable.create();
        
        public Builder add(EventType type, OccupiedBases bases, int errorCount) {
            Multiset<Integer> values = getValueElement(type, bases);
            values.add(errorCount);
            return this;
        }

        private Multiset<Integer> getValueElement(EventType type, OccupiedBases bases) {
            requireNonNull(type);
            requireNonNull(bases);
            Multiset<Integer> values = data.get(type, bases);
            if (values == null) {
                values = HashMultiset.create();
                data.put(type, bases, values);
            }
            return values;
        }
        
        public Builder setCount(EventType type, OccupiedBases bases, int errorCount, int occurrences) {
            Multiset<Integer> values = getValueElement(type, bases);
            values.setCount(errorCount, occurrences);
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
