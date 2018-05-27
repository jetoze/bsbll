package bsbll.research.pbpf;

import java.io.File;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.collections.Zip;

public class ThreeOutsPerInningVerifier extends GameHandler {

    @Override
    public void onEndOfInning(Inning inning, 
                              ImmutableList<EventField> fields,
                              ImmutableList<PlayOutcome> plays) {
        if (isInvalidOutCount(inning, plays)) {
            reportSuspectInning(inning, fields, plays);
        }
    }
    
    private static boolean isInvalidOutCount(Inning inning, ImmutableList<PlayOutcome> plays) {
        // We will get false positives in games that were stopped short of 9 innings,
        // e.g. because of rain or darkness. Not much to do about that.
        int outs = countOuts(plays);
        return (outs > 3) || ((outs < 3) && !inning.isWalkOffPossible());
    }

    private static int countOuts(ImmutableList<PlayOutcome> plays) {
        return plays.stream()
                .mapToInt(PlayOutcome::getNumberOfOuts)
                .sum();
    }

    private void reportSuspectInning(Inning inning, 
                                     ImmutableList<EventField> fields,
                                     ImmutableList<PlayOutcome> plays) {
        System.err.println("File: " + getCurrentFile().getPath());
        System.err.println("Game ID: " + getCurrentGameId());
        System.err.println("Inning: " + inning);
        System.err.println("Outs: " + countOuts(plays));
        Zip.zip(fields, plays, this::printFieldAndOutsMade);
        System.err.println(Strings.repeat("----", 4));
    }
    
    private void printFieldAndOutsMade(EventField field, PlayOutcome play) {
        System.err.println(field + ": [" + play.getNumberOfOuts() + "]");
    }

    
    public static void main(String[] args) {
        Year year = Year.of(1928);
        File folder = PlayByPlayFileUtils.getFolder(year);
        ThreeOutsPerInningVerifier v = new ThreeOutsPerInningVerifier();
        v.parseAll(folder);
    }
}
