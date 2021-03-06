package bsbll.research.pbpf;

import java.util.Comparator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.OccupiedBases;
import bsbll.game.params.OutAdvanceDistribution;
import bsbll.game.params.OutAdvanceDistributionFactory;
import bsbll.game.params.OutAdvanceKey;
import bsbll.game.params.OutLocation;
import bsbll.game.play.EventType;

public final class DistributionOfAdvancesOnOut {

    private DistributionOfAdvancesOnOut() {/**/}

    private static void report(Year year, OutAdvanceDistribution distribution) {
        System.out.println("Distribution of Advances on Outs for the Year " + year);
        String keySep = Strings.repeat("=", 30);
        String basesSep = "  " + Strings.repeat("-", 26);
        
        // The order in which we will present the individual advances
        Comparator<Multiset.Entry<Advances>> dOrder = presentationOrder();

        for (OutLocation location : OutLocation.values()) {
            for (int outs : new int[] { 0, 1, 2 }) {
                OutAdvanceKey key = OutAdvanceKey.of(EventType.OUT, location, outs);
                System.out.println(key);
                System.out.println(keySep);
                ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> distributionForHitType = distribution.forKey(key);
                distributionForHitType.keySet().stream()
                    .sorted()
                    .forEach(b -> {
                        System.out.println(b);
                        distributionForHitType.get(b).entrySet().stream()
                            .sorted(dOrder)
                            .map(as -> String.format("  %-20s%6d", as.getElement(), as.getCount()))
                            .forEach(System.out::println);
                        System.out.println(basesSep);
                        System.out.println(String.format("  %-20s%6d", "Total", distributionForHitType.get(b).size()));
                        System.out.println(basesSep);
                    });
                System.out.println(keySep);
                System.out.println();
            }
        }
    }
    
    private static Comparator<Multiset.Entry<Advances>> presentationOrder() {
        Comparator<Multiset.Entry<Advances>> dOrder = Comparator.comparing(Multiset.Entry::getCount);
        return dOrder.reversed();
    }


    public static void main(String[] args) {
        Year year = Year.of(1925);
        
        OutAdvanceDistributionFactory factory = OutAdvanceDistributionFactory.retrosheet(year);
        OutAdvanceDistribution d = factory.createDistribution();
        
        report(year, d);
    }
}
