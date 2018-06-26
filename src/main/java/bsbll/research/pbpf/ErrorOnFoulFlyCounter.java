package bsbll.research.pbpf;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import bsbll.Year;
import bsbll.game.play.EventType;
import bsbll.player.PlayerId;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class ErrorOnFoulFlyCounter extends GameHandler {
    // TODO: Generalize me, by taking the type of event as input. Also, the output
    // should be a distribution, since the event can happen more than once per AT BAT.
    // So, output should be:
    //    + Number of plate appearances;
    //    + Number of times the event happened once during a plate appearance;
    //    + Number of times the event happened twice during a plate appearance;
    //    + Etc
    private int plateAppearances;
    private final Multiset<Integer> distribution = HashMultiset.create();
    
    @Override
    public void onEndOfInning(Inning inning, ImmutableList<ParsedPlay> plays) {
        PlayerId lastBatter = null;
        int eventsDuringPA = 0;
        for (ParsedPlay play : plays) {
            if (lastBatter == null || !play.getBatterId().equals(lastBatter)) {
                // This logic is not correct, since a plate appearance can span over
                // multiple innings (e.g. the last out is made on a caught stealing).
                ++plateAppearances;
                if (eventsDuringPA > 0) {
                    distribution.add(eventsDuringPA);
                }
                lastBatter = play.getBatterId();
                eventsDuringPA = 0;
            }
            if (play.getType() == EventType.ERROR_ON_FOUL_FLY) {
                ++eventsDuringPA;
            }
        }
        if (eventsDuringPA > 0) {
            distribution.add(eventsDuringPA);
        }
    }
    
    public void report(Year year) {
        System.out.println("Error on Foul Fly Distribution in " + year);
        System.out.println("Plate Appearances: " + plateAppearances);
        for (Multiset.Entry<Integer> e : Multisets.copyHighestCountFirst(distribution).entrySet()) {
            System.out.println(String.format("%s: %s time(s)", e.getElement(), e.getCount()));
        }
    }
    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        ErrorOnFoulFlyCounter counter = new ErrorOnFoulFlyCounter();
        counter.parseAll(PlayByPlayFileUtils.getFolder(year));
        counter.report(year);
    }
}
