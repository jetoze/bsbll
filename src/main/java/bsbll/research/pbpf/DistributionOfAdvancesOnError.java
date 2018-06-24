package bsbll.research.pbpf;

import bsbll.Year;
import bsbll.game.params.ErrorAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceDistributionFactory;

public final class DistributionOfAdvancesOnError {

    public DistributionOfAdvancesOnError() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        ErrorAdvanceDistribution ds = ErrorAdvanceDistributionFactory.retrosheet(year).createDistribution();
        ds.keysSet().stream()
            .map(Object::toString)
            .sorted()
            .forEach(System.out::println);
        //ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> d = ds.forKey(ErrorAdvanceKey.of(EventType.OUT, 1, 2));
        //System.out.println(d.get(OccupiedBases.FIRST_AND_SECOND));
    }

}
