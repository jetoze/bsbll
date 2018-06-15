package bsbll.research.pbpf;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import bsbll.Year;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class FieldersChoiceRatio extends GameHandler {
    private final Multiset<Set<Base>> outsWithRunnersOnBase = HashMultiset.create();
    private final Multiset<Set<Base>> fieldersChoices = HashMultiset.create();
    
    private int playerId;

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        BaseSituation bases = BaseSituation.empty();
        for (PlayOutcome p : plays) {
            if (!bases.isEmpty()) {
                EnumSet<Base> occupied = bases.getOccupiedBases();
                if (p.getType() == EventType.OUT) {
                    outsWithRunnersOnBase.add(occupied);
                } else if (p.getType() == EventType.FIELDERS_CHOICE) {
                    fieldersChoices.add(occupied);
                }
            }
            bases = bases.advanceRunners(nextBatter(), p.getAdvances()).getNewSituation();
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
    
    private void report(Year year) {
        int outCount = outsWithRunnersOnBase.size();
        int fcCount = fieldersChoices.size();
        System.out.println("Fielder's Choices Compared to Outs with Runners on Base for the Year " + year);
        System.out.println();
        System.out.println("TOTALS:");
        System.out.println("Fielders Choices: " + fcCount);
        System.out.println("Outs with Runners on Base: " + outCount);
        System.out.println(String.format("FC / (FC + Outs): %.3f", 
                (1.0 * fcCount) / (fcCount + outCount)));
        System.out.println();
        for (ImmutableSet<Base> p : Base.occupiedBasesPossibilities()) {
            System.out.println(p);
            int o = outsWithRunnersOnBase.count(p);
            int f = fieldersChoices.count(p);
            System.out.println(String.format("  Outs: %d, FCs: %d, %%: %.3f", o, f, (1.0 * f) / o));
        }
    }

    public static void main(String[] args) {
        Year year = Year.of(1969);
        File folder = PlayByPlayFileUtils.getFolder(year);
        FieldersChoiceRatio r = new FieldersChoiceRatio();
        r.parseAll(folder);
        r.report(year);
    }
}
