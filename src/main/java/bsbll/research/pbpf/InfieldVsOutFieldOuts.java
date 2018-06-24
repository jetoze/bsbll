package bsbll.research.pbpf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.function.Predicate;

import bsbll.Year;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import tzeth.strings.Padding;

public final class InfieldVsOutFieldOuts implements PlayByPlayFile.Callback {
    private final Year year;
    private int infieldOuts;
    private int outfieldOuts;

    private InfieldVsOutFieldOuts(Year year) {
        this.year = year;
    }

    @Override
    public Predicate<PlayOutcome> outcomeFilter() {
        return po -> po.getType() == EventType.OUT;
    }

    @Override
    public void onEvent(ParsedPlay play) {
        String basicPlay = play.getEventField().getBasicPlay();
        char c = basicPlay.charAt(0);
        if (Character.isDigit(c)) {
            if (play.isOutfieldOut()) {
                ++outfieldOuts;
            } else {
                ++infieldOuts;
            }
        } else {
            // ??
            System.out.println(play.getEventField().getRawString());
        }
    }

    private void report() {
        System.out.println("Infield vs Outfield Outs for the Year " + year + ":");
        System.out.println();
        Padding labelPadding = Padding.of(24);
        Padding countPadding = Padding.of(6);
        System.out.println(labelPadding.right("Infield Outs:") + countPadding.left(infieldOuts));
        System.out.println(labelPadding.right("Outfield Outs:") + countPadding.left(outfieldOuts));
        double ratio = (1.0 * infieldOuts) / (outfieldOuts);
        System.out.println(labelPadding.right("Ratio:") + 
                countPadding.left(new DecimalFormat("0.000").format(ratio)));
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        InfieldVsOutFieldOuts s = new InfieldVsOutFieldOuts(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        PlayByPlayFile.parseAll(folder, s);
        s.report();
    }
}
