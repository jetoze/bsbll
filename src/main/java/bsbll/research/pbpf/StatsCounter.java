package bsbll.research.pbpf;

import java.io.File;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.strings.Padding;

/**
 * Sums the number of games, (half) innings, hits, runs, errors, and plays
 * across one or more play-by-play file.
 */
public final class StatsCounter extends GameHandler {
    private int games;
    private int innings;
    private int hits;
    private int runs;
    private int errors;
    private int plays;

    @Override
    public void onStartGame(String id) {
        ++games;
    }

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        ++innings;
        this.plays += plays.size();
        for (PlayOutcome p : plays) {
            if (p.getType().isHit()) {
                ++hits;
            }
            runs += p.getNumberOfRuns();
            errors += p.getNumberOfErrors();
        }
    }
    
    public void report() {
        Padding labelPadding = Padding.of(14);
        System.out.println(labelPadding.right("Games:") + games);
        System.out.println(labelPadding.right("Half-innings:") + innings);
        System.out.println(labelPadding.right("Plays:")  + plays);
        System.out.println(labelPadding.right("Hits:")  + hits);
        System.out.println(labelPadding.right("Runs:")  + runs);
        System.out.println(labelPadding.right("Errors:")  + errors);
    }

    public static void main(String[] args) {
        Year year = Year.of(1969);
        File folder = PlayByPlayFileUtils.getFolder(year);
        StatsCounter ec = new StatsCounter();
        ec.parseAll(folder);
        ec.report();
    }
}
