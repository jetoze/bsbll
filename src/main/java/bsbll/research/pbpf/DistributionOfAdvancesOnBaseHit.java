package bsbll.research.pbpf;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseHit;
import bsbll.game.params.BaseHitAdvanceDistribution;
import bsbll.game.params.BaseHitAdvanceDistributionFactory;

public final class DistributionOfAdvancesOnBaseHit {

    private DistributionOfAdvancesOnBaseHit() {/**/}

    private static void report(Year year, BaseHitAdvanceDistribution distribution) {
        System.out.println("Distribution of Advances on Base Hits for the Year " + year);
        String typeOfHitSep = Strings.repeat("=", 30);
        String basesSep = "  " + Strings.repeat("-", 26);
        
        // The order in which we will present the occupied-bases states
        Comparator<EnumSet<Base>> bOrder = Comparator.comparing(EnumSet::size);
        bOrder = bOrder.thenComparing(s -> s.iterator().next()); // Java won't allow me to do this in one single expression
        
        // The order in which we will present the individual advances
        Comparator<Multiset.Entry<Advances>> dOrder = presentationOrder();

        for (BaseHit typeOfHit : Arrays.asList(BaseHit.SINGLE, BaseHit.DOUBLE, BaseHit.TRIPLE)) {
            System.out.println(typeOfHit);
            System.out.println(typeOfHitSep);
            ImmutableMap<EnumSet<Base>, Multiset<Advances>> distributionForHitType = distribution.forHit(typeOfHit);
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
            System.out.println(typeOfHitSep);
            System.out.println();
        }
    }
    
    private static Comparator<Multiset.Entry<Advances>> presentationOrder() {
        Comparator<Multiset.Entry<Advances>> dOrder = Comparator.comparing(Multiset.Entry::getCount);
        return dOrder.reversed();
    }


    public static void main(String[] args) {
        Year year = Year.of(1925);
        
        BaseHitAdvanceDistributionFactory factory = BaseHitAdvanceDistributionFactory.retrosheet(year);
        BaseHitAdvanceDistribution d = factory.createDistribution();
        
        report(year, d);
    }
}
