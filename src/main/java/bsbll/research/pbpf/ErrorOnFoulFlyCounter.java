package bsbll.research.pbpf;

import java.text.DecimalFormat;

import com.google.common.collect.ImmutableList;

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
    private int errors;
    
    @Override
    public void onEndOfInning(Inning inning, ImmutableList<ParsedPlay> plays) {
        PlayerId lastBatter = null;
        for (ParsedPlay play : plays) {
            if (lastBatter == null || !play.getBatterId().equals(lastBatter)) {
                // This logic is not correct, since a plate appearance can span over
                // multiple innings (e.g. the last out is made on a caught stealing).
                ++plateAppearances;
                lastBatter = play.getBatterId();
            }
            if (play.getType() == EventType.ERROR_ON_FOUL_FLY) {
                ++errors;
            }
        }
    }
    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        ErrorOnFoulFlyCounter counter = new ErrorOnFoulFlyCounter();
        counter.parseAll(PlayByPlayFileUtils.getFolder(year));
        System.out.println("Plate Appearances: " + counter.plateAppearances);
        System.out.println("Erorr on Foul Fly Occurences: " + counter.errors);
        System.out.println("Pct: " + new DecimalFormat("0.0000").format((1.0 * counter.errors) / counter.plateAppearances));
    }
}
