package bsbll.research.pbpf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.function.Predicate;

import bsbll.Year;
import bsbll.bases.Advance;
import bsbll.bases.Base;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import tzeth.strings.Padding;

public final class RunnerOnFirstThrownOutAtHomeOnTriple implements PlayByPlayFile.Callback {
    private final Year year;
    /**
     * The number of times a triple was hit with a runner on first.
     */
    private int sampleSize;
    /**
     * The number of times the runner on first was thrown out at home.
     */
    private int occurrences;
    
    private RunnerOnFirstThrownOutAtHomeOnTriple(Year year) {
        this.year = year;
    }

    @Override
    public void onEvent(ParsedPlay play) {
        ++sampleSize;
        if (play.getAdvances().contains(Advance.out(Base.FIRST, Base.HOME))) {
            System.out.println(play.getEventField().getRawString());
            ++occurrences;
        } else if (!play.getAdvances().contains(Advance.safe(Base.FIRST, Base.HOME))) {
            // huh?
            System.err.println(play.getAdvances());
        }
    }

    @Override
    public Predicate<PlayOutcome> outcomeFilter() {
        return o -> (o.getType() == EventType.TRIPLE) && o.getAdvances().contains(Base.FIRST);
    }
    
    private void report() {
        System.out.println("Runner on Third Out at Home on Triple for the Year " + year + ":");
        System.out.println();
        Padding labelPadding = Padding.of(16);
        Padding countPadding = Padding.of(8);
        System.out.println(labelPadding.right("Sample size:") + 
                countPadding.left(sampleSize));
        System.out.println(labelPadding.right("Occurrences:") + countPadding.left(occurrences));
        double ratio = (1.0 * occurrences) / (sampleSize);
        System.out.println(labelPadding.right("Probability:") + 
                countPadding.left(new DecimalFormat("0.000").format(ratio)));
    }

    public static void main(String[] args) {
        Year year = Year.of(1926);
        RunnerOnFirstThrownOutAtHomeOnTriple r = new RunnerOnFirstThrownOutAtHomeOnTriple(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, r);
        r.report();
    }
}
