package bsbll.research.pbpf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.function.Predicate;

import bsbll.Year;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;

public final class ScoredOnTriplesPlusError implements PlayByPlayFile.Callback {
    private final Year year;
    private int triples;
    private int runs;

    public ScoredOnTriplesPlusError(Year year) {
            this.year = year;
        }

    @Override
    public Predicate<PlayOutcome> outcomeFilter() {
        return o -> o.getType() == EventType.TRIPLE;
    }

    @Override
    public void onEvent(ParsedPlay play) {
        assert play.getType() == EventType.TRIPLE;
        ++triples;
        Advances advances = play.getAdvances();
        if (advances.didRunnerScore(Base.HOME)) {
            ++runs;
        }
    }

    public void report() {
        System.out.println("Batter Scored on Triple for the Year " + year + ":");
        System.out.println();
        System.out.println("Triples: " + triples);
        System.out.println("Scored: " + runs);
        System.out.println("Percentage: " + new DecimalFormat("#.000").format((1.0 * runs) / (triples)));
    }

    public static void main(String[] args) throws Exception {
        Year year = Year.of(1925);
        ScoredOnTriplesPlusError s = new ScoredOnTriplesPlusError(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, s);
        s.report();
    }
}