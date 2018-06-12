package bsbll.research.pbpf;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import bsbll.Base;
import bsbll.Year;
import bsbll.game.BaseSituation;
import bsbll.player.Player;
import bsbll.research.Advances;
import bsbll.research.EventField;
import bsbll.research.EventType;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class DistributionOfAdvancesOnBaseHit extends GameHandler {
    private final Year year;
    // Maps a state, given by the currently occupied bases, to the resulting distribution
    // of advances after a double, for each type of base hit (except homeruns)
    private final Table<EventType, EnumSet<Base>, Multiset<Advances>> distributions = HashBasedTable.create();
    
    private int playerId;

    private DistributionOfAdvancesOnBaseHit(Year year) {
        this.year = year;
    }

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        BaseSituation baseSituation = BaseSituation.empty();
        for (PlayOutcome play : plays) {
            Player batter = nextBatter();
            if (play.isBaseHit() && play.getNumberOfErrors() == 0) {
                update(play.getType(), baseSituation, play.getAdvances());
            }
            baseSituation = play.applyTo(batter, baseSituation);
        }
    }
    
    private void update(EventType typeOfHit, BaseSituation situation, Advances advances) {
        if (typeOfHit == EventType.HOMERUN) {
            return;
        }
        EnumSet<Base> occupied = situation.getOccupiedBases();
        Multiset<Advances> distribution = this.distributions.get(typeOfHit, occupied);
        if (distribution == null) {
            distribution = HashMultiset.create();
            this.distributions.put(typeOfHit, occupied, distribution);
        }
        distribution.add(advances);
    }
    
    private void report() {
        System.out.println("Distribution of Advances on Base Hits for the Year " + year);
        String typeOfHitSep = Strings.repeat("=", 30);
        String basesSep = "  " + Strings.repeat("-", 26);
        
        // The order in which we will present the occupied-bases states
        Comparator<EnumSet<Base>> bOrder = Comparator.comparing(EnumSet::size);
        bOrder = bOrder.thenComparing(s -> s.iterator().next()); // Java won't allow me to do this in one single expression
        
        // The order in which we will present the individual advances
        Comparator<Multiset.Entry<Advances>> dOrder = presentationOrder();

        for (EventType typeOfHit : Arrays.asList(EventType.SINGLE, EventType.DOUBLE, EventType.TRIPLE)) {
            System.out.println(typeOfHit);
            System.out.println(typeOfHitSep);
            Map<EnumSet<Base>, Multiset<Advances>> distributionForHitType = this.distributions.row(typeOfHit);
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
    
    /**
     * We generate a new Player for each play. This is obviously not realistic,
     * but that is irrelevant - we just need Players to move around the bases.
     * See corresponding XXX comment in BaseSituation, about making that class generic.
     */
    private Player nextBatter() {
        ++playerId;
        return new Player(Integer.toString(playerId), "John Doe");
    }


    public static void main(String[] args) {
        Year year = Year.of(1925);
        DistributionOfAdvancesOnBaseHit d = new DistributionOfAdvancesOnBaseHit(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        d.parseAll(folder);
        d.report();
    }
}
