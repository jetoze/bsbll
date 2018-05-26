package bsbll.research;

import java.io.File;
import java.util.function.Predicate;

import bsbll.Base;
import bsbll.Year;

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
    public void onEvent(EventField field, PlayOutcome outcome) {
        assert outcome.getType() == EventType.TRIPLE;
        ++triples;
        Advances advances = outcome.getAdvances();
        if (advances.didRunnerScore(Base.HOME)) {
            ++runs;
        }
    }

    public void report() {
        System.out.println("Batter Scored on Triple for the Year " + year + ":");
        System.out.println();
        System.out.println("Triples: " + triples);
        System.out.println("Scored: " + runs);
    }

    public static void main(String[] args) throws Exception {
        Year year = Year.of(1925);
        File folder = PlayByPlayFileUtils.getFolder(year);
        ScoredOnTriplesPlusError s = new ScoredOnTriplesPlusError(year);
        PlayByPlayFile.parseAll(folder, s);
        s.report();
    }
}