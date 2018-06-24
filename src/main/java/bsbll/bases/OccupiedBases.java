package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum OccupiedBases implements Iterable<Base> {
    NONE(ImmutableSet.of()),
    FIRST(ImmutableSet.of(Base.FIRST)),
    SECOND(ImmutableSet.of(Base.SECOND)),
    THIRD(ImmutableSet.of(Base.THIRD)),
    FIRST_AND_SECOND(ImmutableSet.of(Base.FIRST, Base.SECOND)),
    FIRST_AND_THIRD(ImmutableSet.of(Base.FIRST, Base.THIRD)),
    SECOND_AND_THIRD(ImmutableSet.of(Base.SECOND, Base.THIRD)),
    LOADED(ImmutableSet.of(Base.FIRST, Base.SECOND, Base.THIRD));
    
    private static final ImmutableMap<Base, OccupiedBases> singleOccupancyInstances = ImmutableMap.of(
            Base.FIRST, FIRST,
            Base.SECOND, SECOND,
            Base.THIRD, THIRD);
    
    private static final ImmutableMap<ImmutableSet<Base>, OccupiedBases> doubleOccupancyInstances = ImmutableMap.of(
            ImmutableSet.of(Base.FIRST, Base.SECOND), FIRST_AND_SECOND,
            ImmutableSet.of(Base.FIRST, Base.THIRD), FIRST_AND_THIRD,
            ImmutableSet.of(Base.SECOND, Base.THIRD), SECOND_AND_THIRD);
    
    private final ImmutableSet<Base> bases;
    
    private OccupiedBases(Set<Base> bases) {
        this.bases = Sets.immutableEnumSet(bases);
    }
    
    public static OccupiedBases of(Set<Base> bases) {
        if (bases.isEmpty()) {
            return NONE;
        }
        checkArgument(bases.stream().allMatch(Base::isOccupiable));
        if (bases.size() == 1) {
            return singleOccupancyInstances.get(bases.iterator().next());
        } else if (bases.size() == 2) {
            return doubleOccupancyInstances.get(bases);
        } else {
            assert bases.size() == 3;
            return LOADED;
        }
    }

    public boolean isEmpty() {
        return this == NONE;
    }
    
    public boolean contains(Base b) {
        requireNonNull(b);
        return this.bases.contains(b);
    }
    
    @Override
    public Iterator<Base> iterator() {
        return bases.iterator();
    }

    public Stream<Base> stream() {
        return bases.stream();
    }
    
    @Override
    public void forEach(Consumer<? super Base> action) {
        requireNonNull(action);
        bases.forEach(action);
    }

    @Override
    public String toString() {
        return bases.toString();
    }
}
