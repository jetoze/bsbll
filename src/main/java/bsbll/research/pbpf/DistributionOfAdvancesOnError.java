package bsbll.research.pbpf;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.OccupiedBases;
import bsbll.game.params.ErrorAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceDistributionFactory;
import bsbll.game.params.ErrorAdvanceKey;
import bsbll.game.play.EventType;

public final class DistributionOfAdvancesOnError {

    public DistributionOfAdvancesOnError() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        ErrorAdvanceDistribution ds = ErrorAdvanceDistributionFactory.retrosheet(year).createDistribution();
        ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> d = ds.forKey(ErrorAdvanceKey.of(EventType.OUT, 1));
        System.out.println(d.get(OccupiedBases.FIRST));
    }

}
