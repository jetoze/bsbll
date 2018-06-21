package bsbll.research.pbpf;

import java.io.File;
import java.util.Iterator;
import java.util.function.Predicate;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import bsbll.Year;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.research.EventField;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class ErrorCounter extends GameHandler {
    private int errors;
    private final Predicate<PlayOutcome> filter = hitsAndOutsOnly().negate();
    private final Multiset<EventType> distribution = HashMultiset.create();

    public ErrorCounter() {
        //super(id -> id.equals("CHN192504160"));
    }
    
    public static Predicate<PlayOutcome> hitsAndOutsOnly() {
        return p -> {
            EventType t = p.getType();
            return t.isHit() || (t == EventType.OUT) || (t == EventType.REACHED_ON_ERROR);
        };
    }
    
    @Override
    public void onEndOfInning(Inning inning, 
                              ImmutableList<EventField> fields,
                              ImmutableList<PlayOutcome> plays) {
        Iterator<EventField> itF = fields.iterator();
        Iterator<PlayOutcome> itP = plays.iterator();
        while (itF.hasNext() && itP.hasNext()) {
            PlayOutcome p = itP.next();
            EventField f = itF.next();
            if (p.getNumberOfErrors() > 0 && filter.test(p)) {
                distribution.add(p.getType());
                System.out.println(String.format("%s -- %s: %s - [%s]", 
                        getCurrentFile().getName(), getCurrentGameId(), p, f));
            }
        }
        
        errors += plays.stream()
                .filter(filter.negate())
                .mapToInt(PlayOutcome::getNumberOfErrors)
                .sum();
    }

    private void report(Year year) {
        //System.out.println("Total Number of Errors on Hits, Outs, and Reached-On-Error in 1925: " + errors);
        //System.out.println("Total Number of Errors in " + year + ": " + errors);
        System.out.println();
        Multisets.copyHighestCountFirst(distribution).forEachEntry((t, v) -> System.out.println(t.name() + ": " + v));
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        
        ErrorCounter ec = new ErrorCounter();
        File folder = PlayByPlayFileUtils.getFolder(year);
        ec.parseAll(folder);
//        PlayByPlayFile file = PlayByPlayFile.of("/Users/torgil/coding/data/bsbll/play-by-play-files/1925/1925CHN.EVN");
//        ec.parse(file);
        
        ec.report(year);
    }
}
