package bsbll.research;

import java.util.function.Consumer;
import java.util.function.Predicate;

import bsbll.Base;
import bsbll.Year;

public final class ScoredOnTriplesPlusError {
    private final Year year;
    private int triples;
    private int scored;
    
    private ScoredOnTriplesPlusError(Year year) {
        this.year = year;
    }
    
    public void run() throws Exception {
        Predicate<EventType> typeFilter = Predicate.isEqual(EventType.TRIPLE);
        PlayByPlayFileUtils.parseAllPlays(year, typeFilter, new Callback());
        report();
    }
    
    private void report() {
        System.out.println("Batter Scored on Triple for the Year " + year + ":");
        System.out.println();
        System.out.println("Triples: " + triples);
        System.out.println("Scored: " + scored);
    }
    
    
    private class Callback implements Consumer<PlayOutcome> {

        @Override
        public void accept(PlayOutcome outcome) {
            assert outcome.getType() == EventType.TRIPLE;
            ++triples;
            Advances advances = outcome.getAdvances();
            if (advances.didRunnerScore(Base.HOME)) {
                ++scored;
            }
        }
    }

    
    public static void main(String[] args) throws Exception {
        ScoredOnTriplesPlusError s = new ScoredOnTriplesPlusError(Year.of(1927));
        s.run();
    }
    
}
