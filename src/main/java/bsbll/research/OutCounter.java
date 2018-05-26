package bsbll.research;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bsbll.Year;
import bsbll.research.PlayByPlayFile.Inning;

/**
 * Verifies our parsing of play-by-play files by checking that each inning
 * (except possibly the last one in each game) has exactly three outs.
 */
public final class OutCounter implements PlayByPlayFile.Callback {
    private int outs;
    private List<String> playsInInning;
    
    @Override
    public void onStartGame(String id) {
        System.out.println(id);
        this.outs = 0;
    }

    @Override
    public void onStartInning(Inning inning) {
        int expectedOutsCount = (inning.getNumber() == 1 && inning.getHalf() == Inning.TOP)
                ? 0
                : 3;
        if (this.outs != expectedOutsCount) {
            for (String s : playsInInning) {
                PlayOutcome outcome = EventParser.parse(s);
                System.err.println(s + ": " + outcome.getNumberOfOuts());
            }
        }
//        checkState(this.outs == expectedOutsCount, "Expected %s outs when starting the %s, but found %s. Plays: ",
//                expectedOutsCount, inning, this.outs, this.playsInInning);
        this.outs = 0;
        this.playsInInning = new ArrayList<>();
    }

    @Override
    public void onEvent(EventField field, PlayOutcome outcome) {
        this.playsInInning.add(field.getRawString());
        this.outs += outcome.getNumberOfOuts();
    }

    public static void main(String[] args) {
        Year year = Year.of(1928);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, new OutCounter());
    }

}
