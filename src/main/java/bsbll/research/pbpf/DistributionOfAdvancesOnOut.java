package bsbll.research.pbpf;

import java.util.Comparator;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.game.params.OutAdvanceDistribution;
import bsbll.game.params.OutAdvanceDistributionFactory;
import bsbll.game.params.OutLocation;

public final class DistributionOfAdvancesOnOut {

    private DistributionOfAdvancesOnOut() {/**/}

    private static void report(Year year, OutAdvanceDistribution distribution) {
        System.out.println("Distribution of Advances on Outs for the Year " + year);
        String locationtSep = Strings.repeat("=", 30);
        String basesSep = "  " + Strings.repeat("-", 26);
        
        // The order in which we will present the occupied-bases states
        Comparator<ImmutableSet<Base>> bOrder = Comparator.comparing(Set::size);
        bOrder = bOrder.thenComparing(s -> s.iterator().next()); // Java won't allow me to do this in one single expression
        
        // The order in which we will present the individual advances
        Comparator<Multiset.Entry<Advances>> dOrder = presentationOrder();

        for (OutLocation location : OutLocation.values()) {
            System.out.println(location);
            System.out.println(locationtSep);
            ImmutableMap<ImmutableSet<Base>, ImmutableMultiset<Advances>> distributionForHitType = distribution.forKey(location);
            distributionForHitType.keySet().stream()
                .sorted(bOrder)
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
            System.out.println(locationtSep);
            System.out.println();
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
