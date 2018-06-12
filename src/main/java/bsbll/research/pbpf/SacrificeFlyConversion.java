package bsbll.research.pbpf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;
import tzeth.strings.Padding;

public final class SacrificeFlyConversion extends GameHandler {
    private final Year year;
    /**
     * "opportunities" is really a misnomer. This counts the number of occurrences where
     * an outfield out was made with a runner on 3rd and less than two outs.
     */
    private int opportunities;
    /**
     * The number of "opportunities" that actually resulted in a sacrifice fly.
     */
    private int conversions;
    private int playerId;
    
    private SacrificeFlyConversion(Year year) {
        this.year = year;
    }

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        int outs = 0;
        BaseSituation bases = BaseSituation.empty();
        Iterator<EventField> itF = fields.iterator();
        Iterator<PlayOutcome> itP = plays.iterator();
        while (itF.hasNext() && itP.hasNext()) {
            PlayOutcome p = itP.next();
            EventField f = itF.next();
            boolean isOpportunity = (outs < 2) && bases.isOccupied(Base.THIRD) && f.isOutfieldOut();
            if (isOpportunity) {
                ++opportunities;
                if (f.isSacrificeFly()) {
                    ++conversions;
                }
            } else if (f.isSacrificeFly()) {
                // Two ways this can happen:
                //   1. The out was actually made by an infielder, e.g. "5/P5DF/FL/SF.3-H"
                //   2. There was an error on the play, allowing other runners to advance, e.g. "E8/F8D/SF.3-H(RBI);1-2"
                // How do we handle this?
                // TODO: Come up with counts for these events too, so that we can simulate it accordingly.
            }
            bases = p.applyTo(nextBatter(), bases);
        }
    }

    /**
     * We generate a new Player for each play. This is obviously not realistic,
     * but that is irrelevant - we just need Players to move around the bases.
     * See corresponding XXX comment in BaseSituation, about making that class generic.
     */
    private Player nextBatter() {
        ++playerId;
        return new Player(Integer.toString(playerId), "John Doe");
    }
    
    private void report() {
        System.out.println("Sacrifice Fly Conversion Rate for the Year " + year + ":");
        System.out.println();
        Padding labelPadding = Padding.of(16);
        Padding countPadding = Padding.of(8);
        System.out.println(labelPadding.right("Opportunities(*):") + 
                countPadding.left(opportunities));
        System.out.println(labelPadding.right("Sacrifice Flies:") + countPadding.left(conversions));
        double ratio = (1.0 * conversions) / (opportunities);
        System.out.println(labelPadding.right("Conversion Rate:") + 
                countPadding.left(new DecimalFormat("0.000").format(ratio)));
        System.out.println();
        System.out.println("(*) Number of outfield outs withrRunner on 3rd and less than two outs");
    }
    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        SacrificeFlyConversion s = new SacrificeFlyConversion(year);
        File folder = PlayByPlayFileUtils.getFolder(year);
        s.parseAll(folder);
        s.report();
    }
}
