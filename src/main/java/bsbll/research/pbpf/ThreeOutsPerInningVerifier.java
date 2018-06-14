package bsbll.research.pbpf;

import java.io.File;
import java.util.Iterator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.collections.Zip;
import tzeth.strings.Padding;

/**
 * Verifies our parsing of play-by-play files, by verifiying that there are
 * exactly three outs in every inning, excepting possibly walk-off innings.
 * <p>
 * Note that this class will currently identify false positives in cases where a
 * game was called in the middle of an ongoing inning, e.g. due to darkness or
 * rain. Each violation must therefore be investigated to determine if it indeed
 * due to a parsing problem.
 */
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
        Padding labelPadding = Padding.of(9);
        System.err.println(labelPadding.right("File:") + getCurrentFile().getPath());
        System.err.println(labelPadding.right("Game ID:") + getCurrentGameId());
        System.err.println(labelPadding.right("Inning:") + inning);
        System.err.println(labelPadding.right("Outs:") + countOuts(plays));
        System.err.println("Plays:");
        int widthOfLongestField = fields.stream()
                .map(EventField::toString)
                .mapToInt(String::length)
                .max()
                .orElse(10);
        Padding fieldPadding = Padding.of(Math.max(widthOfLongestField, 9));
        Iterator<String> fieldStrings = fields.stream()
                .map(EventField::toString)
                .map(fieldPadding::right)
                .iterator();
        Zip.zip(fieldStrings, plays.iterator(), this::printFieldAndOutsMade);
        System.err.println(Strings.repeat("--------", 4));
    }
    
    private void printFieldAndOutsMade(String field, PlayOutcome play) {
        System.err.println(field + " -> [" + play.getNumberOfOuts() + "]");
    }

    
    public static void main(String[] args) {
        Year year = Year.of(1931);
        File folder = PlayByPlayFileUtils.getFolder(year);
        ThreeOutsPerInningVerifier v = new ThreeOutsPerInningVerifier();
        v.parseAll(folder);
    }
}
