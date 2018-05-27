package bsbll.research.pbpf;

import java.io.File;
import java.util.function.Predicate;

import bsbll.Base;
import bsbll.Year;
import bsbll.research.Advances;
import bsbll.research.EventField;
import bsbll.research.EventType;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

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

    @Override
    public void onStartInning(Inning inning) {
        System.out.println(inning);
    }

    public void report() {
        System.out.println("Batter Scored on Triple for the Year " + year + ":");
        System.out.println();
        System.out.println("Triples: " + triples);
        System.out.println("Scored: " + runs);
    }

    public static void main(String[] args) throws Exception {
        Year year = Year.of(1925);
        ScoredOnTriplesPlusError s = new ScoredOnTriplesPlusError(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, s);
        s.report();
    }
}