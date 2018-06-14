package bsbll.research.pbpf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import bsbll.Year;
import bsbll.bases.BaseHit;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;

public final class ErrorOnHit implements PlayByPlayFile.Callback {
    private final EnumMap<BaseHit, ErrorCount> countsByHitType = new EnumMap<>(BaseHit.class);

    @Override
    public Predicate<PlayOutcome> outcomeFilter() {
        return PlayOutcome::isBaseHit;
    }

    @Override
    public void onEvent(EventField field, PlayOutcome outcome) {
        if (outcome.isHomerun()) {
            return;
        }
        ErrorCount count = countsByHitType.computeIfAbsent(BaseHit.fromEventType(outcome.getType()), 
                k -> new ErrorCount());
        count.update(outcome.getNumberOfErrors());
    }
    
    private void report(Year year) {
        System.out.println("Distribution of Number of Errors on Base Hit Plays for the Year " + year);
        String sep = Strings.repeat("=", 30);
        String fmt = "  %-6s:%6s%8s";
        DecimalFormat pct = new DecimalFormat("#.0000");
        for (BaseHit type : BaseHit.otherThanHomerun()) {
            System.out.println(sep);
            System.out.println(type);
            System.out.println(sep);
            ErrorCount counts = countsByHitType.get(type);
            int total = counts.sum();
            counts.stream()
                .map(e -> String.format(fmt, e.getKey(), e.getValue(), pct.format((1.0 * e.getValue()) / total)))
                .forEach(System.out::println);
            System.out.println(String.format(fmt, "Total", total, ""));
        }
    }

    public static void main(String[] args) {
        Year year = Year.of(2001);
        
        ErrorOnHit eoh = new ErrorOnHit();
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, eoh);
        
        eoh.report(year);
    }

    
    private static class ErrorCount {
        private final TreeMap<Integer, Integer> counts = new TreeMap<>();
        
        public void update(int numberOfErrors) {
            counts.merge(numberOfErrors, 1, (v1, v2) -> v1 + v2);
        }
        
        public int sum() {
            return counts.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        public Stream<Map.Entry<Integer, Integer>> stream() {
            return counts.entrySet().stream();
        }
    }
    
}
