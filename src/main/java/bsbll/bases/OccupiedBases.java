package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Immutable
public final class OccupiedBases implements Iterable<Base> {
    // TODO: Should this class be an enum?
    
    private static final OccupiedBases NONE = new OccupiedBases(ImmutableSet.of());
    private static final OccupiedBases FIRST = new OccupiedBases(ImmutableSet.of(Base.FIRST));
    private static final OccupiedBases SECOND = new OccupiedBases(ImmutableSet.of(Base.SECOND));
    private static final OccupiedBases THIRD = new OccupiedBases(ImmutableSet.of(Base.THIRD));
    private static final OccupiedBases FIRST_AND_SECOND = new OccupiedBases(ImmutableSet.of(Base.FIRST, Base.SECOND));
    private static final OccupiedBases FIRST_AND_THIRD = new OccupiedBases(ImmutableSet.of(Base.FIRST, Base.THIRD));
    private static final OccupiedBases SECOND_AND_THIRD = new OccupiedBases(ImmutableSet.of(Base.SECOND, Base.THIRD));
    private static final OccupiedBases LOADED = new OccupiedBases(ImmutableSet.of(Base.FIRST, Base.SECOND, Base.THIRD));
    
    private static final ImmutableList<OccupiedBases> ALL = ImmutableList.of(
            NONE, FIRST, SECOND, THIRD, FIRST_AND_SECOND, FIRST_AND_THIRD, SECOND_AND_THIRD, LOADED);
    
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

    @Override
    public Iterator<Base> iterator() {
        return bases.iterator();
    }

    public Stream<Base> stream() {
        return bases.stream();
    }
    
    public static ImmutableList<OccupiedBases> values() {
        return ALL;
    }
    
    @Override
    public int hashCode() {
        return bases.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof OccupiedBases) {
            return this.bases.equals(((OccupiedBases) obj).bases);
        }
        return false;
    }

    @Override
    public String toString() {
        return bases.toString();
    }
}
