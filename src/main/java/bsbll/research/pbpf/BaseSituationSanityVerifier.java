package bsbll.research.pbpf;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.bases.InvalidBaseSitutationException;
import bsbll.research.EventField;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.collections.Zip;
import tzeth.strings.Padding;

/**
 * Verifies our parsing of play-by-play files, by verifying that there are never
 * a situation where we would have two or more runners on the same base, or that we
 * are trying to advance a runner from a base that was not occupied.
 */
public final class BaseSituationSanityVerifier extends GameHandler {
    private final BaseRunnerFactory baseRunnerFactory = new BaseRunnerFactory();
    
    @Override
    public void onEndOfInning(Inning inning, ImmutableList<ParsedPlay> plays) {
        List<BaseSituation> progression = new ArrayList<>();
        BaseSituation bases = BaseSituation.empty();
        for (ParsedPlay play : plays) {
            try {
                bases = play.applyTo(baseRunnerFactory.getBaseRunner(play), bases);
                progression.add(bases);
            } catch (InvalidBaseSitutationException e) {
                reportProblem(inning, play, plays, progression, e);
                return;
            }
        }
    }
    
    private void reportProblem(Inning inning, 
                               ParsedPlay play,
                               List<ParsedPlay> allPlays,
                               List<BaseSituation> progression,
                               InvalidBaseSitutationException e) {
        Padding labelPadding = Padding.of(9);
        System.err.println(labelPadding.right("File:") + getCurrentFile().getPath());
        System.err.println(labelPadding.right("Game ID:") + getCurrentGameId());
        System.err.println(labelPadding.right("Inning:") + inning);
        System.err.println(labelPadding.right("Field:") + play.getEventField());
        System.err.println(labelPadding.right("Outcome:") + play.getOutcome());
        System.err.println(labelPadding.right("Error:") + e.getMessage());
        System.err.println("Progression:");
        int widthOfLongestField = allPlays.stream()
                .map(ParsedPlay::getEventField)
                .map(EventField::toString)
                .mapToInt(String::length)
                .max()
                .orElse(10);
        Padding fieldPadding = Padding.of(Math.max(widthOfLongestField, 9));
        Iterator<String> fieldStrings = allPlays.stream()
                .map(ParsedPlay::getEventField)
                .map(EventField::toString)
                .map(fieldPadding::right)
                .iterator();
        Zip.zip(fieldStrings, progression.iterator(), this::printFieldAndBaseSituation);
        System.err.println(Strings.repeat("--------", 4));
    }
    
    private void printFieldAndBaseSituation(String field, BaseSituation bases) {
        System.err.println(field + " -> [" + bases + "]");
    }

    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        File folder = PlayByPlayFileUtils.getFolder(year);
        BaseSituationSanityVerifier v = new BaseSituationSanityVerifier();
        v.parseAll(folder);
    }
}
