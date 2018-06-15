package bsbll.research.pbpf;

import java.io.File;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.research.EventField;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class FieldersChoiceRatio extends GameHandler {
    private int outsWithRunnersOnBase;
    private int fieldersChoices;
    
    private int playerId;

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        BaseSituation bases = BaseSituation.empty();
        for (PlayOutcome p : plays) {
            if (p.getType() == EventType.OUT && !bases.isEmpty()) {
                ++outsWithRunnersOnBase;
            } else if (p.getType() == EventType.FIELDERS_CHOICE) {
                ++fieldersChoices;
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
        System.out.println("Fielder's Choices Compared to Outs with Runners on Base for the Year " + year);
        System.out.println("Fielders Choices: " + fieldersChoices);
        System.out.println("Outs with Runners on Base: " + outsWithRunnersOnBase);
        System.out.println(String.format("FC / (FC + Outs): %.3f", 
                (1.0 * fieldersChoices) / (fieldersChoices + outsWithRunnersOnBase)));
    }

    public static void main(String[] args) {
        Year year = Year.of(1925);
        File folder = PlayByPlayFileUtils.getFolder(year);
        FieldersChoiceRatio r = new FieldersChoiceRatio();
        r.parseAll(folder);
        r.report(year);
    }
}
