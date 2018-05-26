package bsbll.research;

import java.io.File;
import java.text.DecimalFormat;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import bsbll.Base;
import bsbll.Year;
import tzeth.strings.Padding;

public final class ReachedOnErrorPercentages implements PlayByPlayFile.Callback {
    private final Year year;
    private int outsPlusErrors;
    private final int[] reachedBaseOnError = new int[4];
    private int thrownOutOnBases;
    
    private ReachedOnErrorPercentages(Year year) {
        this.year = year;
    }
    
    @Override
    public Predicate<PlayOutcome> outcomeFilter() {
        return o -> {
            EventType type = o.getType();
            return (type == EventType.OUT || type == EventType.REACHED_ON_ERROR);
        };
    }

    @Override
    public void onEvent(EventField field, PlayOutcome outcome) {
        ++outsPlusErrors;
        if (outcome.getType() == EventType.OUT) {
            return;
        }
        assert outcome.getType() == EventType.REACHED_ON_ERROR;
        Advances advances = outcome.getAdvances();
        if (advances.contains(Base.HOME)) {
            Advance a = advances.getAdvanceFrom(Base.HOME);
            if (a.isAdvancement()) {
                reachedBaseOnError[a.to().ordinal()]++;
            } else {
                // An error allowed the batter to reach base, but he was thrown out
                // on the follow-up play.
                thrownOutOnBases++;
            }
        } else {
            // If not stated explicitly, it is implied that the batter reached first.
            reachedBaseOnError[0]++;
        }
    }

    public void report() {
        System.out.println("Reached on Error Percentage for the Year " + year + ":");
        System.out.println();
        Padding labelPadding = Padding.of(24);
        Padding countPadding = Padding.of(6);
        Padding pctPadding = Padding.of(10);
        System.out.println(labelPadding.right("Outs + Reached on Error:") + countPadding.left(outsPlusErrors));
        int totalErrors = countErrors();
        printCount("Reached on Error", totalErrors, labelPadding, countPadding, pctPadding);
        for (Base b : Base.values()) {
            printCount("Reached " + b.name().toLowerCase() + ":", reachedBaseOnError[b.ordinal()], 
                    labelPadding, countPadding, pctPadding);
        }
        printCount("Out on advance:", thrownOutOnBases, labelPadding, countPadding, null);
    }
    
    private int countErrors() {
        int e = thrownOutOnBases;
        for (int i : reachedBaseOnError) {
            e += i;
        }
        return e;
    }
    
    private void printCount(String label, 
                            int count,
                            Padding labelPadding,
                            Padding countPadding,
                            @Nullable
                            Padding pctPadding) {
        String line = labelPadding.right(label) + countPadding.left(count);
        if (pctPadding != null) {
            double pct = (1.0 * count) / outsPlusErrors;
            line += pctPadding.left(new DecimalFormat("0.00000").format(pct));
        }
        System.out.println(line);
    }
    
    public void listReachedThirdOnErrorPlays() throws Exception {
        Predicate<String> filter = s -> s.startsWith("E") && s.contains("B-3");
        PlayByPlayFileUtils.collectPlays(year, filter, System.out::println);
    }
    
    public void listThrownOutOnBasesAfterHavingReachedOnErrorPlays() throws Exception {
        Predicate<String> filter = s -> s.startsWith("E") && s.contains("BX");
        PlayByPlayFileUtils.collectPlays(year, filter, System.out::println);
    }
    
    
    public static void main(String[] args) throws Exception {
        Year year = Year.of(1925);
        ReachedOnErrorPercentages s = new ReachedOnErrorPercentages(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, s);
        s.report();
    }
}
