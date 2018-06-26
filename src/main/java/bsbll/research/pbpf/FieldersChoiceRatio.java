package bsbll.research.pbpf;

import java.io.File;
import java.util.EnumSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import bsbll.game.play.EventType;

public final class FieldersChoiceRatio extends DefaultGameHandler {
    private final Multiset<OccupiedBases> outsWithRunnersOnBase = HashMultiset.create();
    private final Multiset<OccupiedBases> fieldersChoices = HashMultiset.create();
    
    public FieldersChoiceRatio() {
        super(EnumSet.of(EventType.OUT, EventType.FIELDERS_CHOICE));
    }

    @Override
    protected void process(ParsedPlay play, BaseSituation bases, int outs) {
        if (bases.areEmpty()) {
            return;
        }
        OccupiedBases occupied = bases.getOccupiedBases();
        if (play.isInfieldOut()) {
            outsWithRunnersOnBase.add(occupied);
        } else if (play.getType() == EventType.FIELDERS_CHOICE) {
            fieldersChoices.add(occupied);
        }
    }
    
    private void report(Year year) {
        int outCount = outsWithRunnersOnBase.size();
        int fcCount = fieldersChoices.size();
        System.out.println("Fielder's Choices Compared to Infield Outs with Runners on Base for the Year " + year);
        System.out.println();
        System.out.println("TOTALS:");
        System.out.println("Fielders Choices: " + fcCount);
        System.out.println("Infield Outs with Runners on Base: " + outCount);
        System.out.println(String.format("FC / (FC + Outs): %.3f", 
                (1.0 * fcCount) / (fcCount + outCount)));
        System.out.println();
        for (OccupiedBases p : OccupiedBases.values()) {
            System.out.println(p);
            int o = outsWithRunnersOnBase.count(p);
            int f = fieldersChoices.count(p);
            System.out.println(String.format("  Outs: %d, FCs: %d, %%: %.3f", o, f, (1.0 * f) / (f + o)));
        }
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        File folder = PlayByPlayFileUtils.getFolder(year);
        FieldersChoiceRatio r = new FieldersChoiceRatio();
        r.parseAll(folder);
        r.report(year);
    }
}
