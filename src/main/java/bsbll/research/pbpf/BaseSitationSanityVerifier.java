package bsbll.research.pbpf;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.game.BaseSituation;
import bsbll.game.InvalidBaseSitutationException;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.collections.Zip;
import tzeth.strings.Padding;

/**
 * Verifies our parsing of play-by-play files, by verifying that there are never
 * a situation where we would have two or more runners on the same base, or that we
 * are trying to advance a runner from a base that was not occupied.
 */
public final class BaseSitationSanityVerifier extends GameHandler {
    private int playerId;
    
    @Override
    public void onEndOfInning(Inning inning, 
                              ImmutableList<EventField> fields,
                              ImmutableList<PlayOutcome> plays) {
        List<BaseSituation> progression = new ArrayList<>();
        BaseSituation bases = BaseSituation.empty();
        Iterator<EventField> itF = fields.iterator();
        Iterator<PlayOutcome> itP = plays.iterator();
        while(itF.hasNext() && itP.hasNext()) {
            EventField field = itF.next();
            PlayOutcome play = itP.next();
            try {
                bases = play.applyTo(nextBatter(), bases);
                progression.add(bases);
            } catch (InvalidBaseSitutationException e) {
                reportProblem(inning, field, play, fields, plays, progression, e);
                return;
            }
        }
    }
    
    private void reportProblem(Inning inning, 
                               EventField field,
                               PlayOutcome play,
                               List<EventField> fields,
                               List<PlayOutcome> allPlays,
                               List<BaseSituation> progression,
                               InvalidBaseSitutationException e) {
        Padding labelPadding = Padding.of(9);
        System.err.println(labelPadding.right("File:") + getCurrentFile().getPath());
        System.err.println(labelPadding.right("Game ID:") + getCurrentGameId());
        System.err.println(labelPadding.right("Inning:") + inning);
        System.err.println(labelPadding.right("Field:") + field);
        System.err.println(labelPadding.right("Outcome:") + play);
        System.err.println(labelPadding.right("Error:") + e.getMessage());
        System.err.println("Progression:");
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
        Zip.zip(fieldStrings, progression.iterator(), this::printFieldAndBaseSituation);
        System.err.println(Strings.repeat("--------", 4));
    }
    
    private void printFieldAndBaseSituation(String field, BaseSituation bases) {
        System.err.println(field + " -> [" + bases + "]");
    }

    private Player nextBatter() {
        ++playerId;
        return new Player(PlayerId.of(Integer.toString(playerId)));
    }

    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        File folder = PlayByPlayFileUtils.getFolder(year);
        BaseSitationSanityVerifier v = new BaseSitationSanityVerifier();
        v.parseAll(folder);
    }
}
