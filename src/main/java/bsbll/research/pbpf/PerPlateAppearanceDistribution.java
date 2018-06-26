package bsbll.research.pbpf;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import bsbll.Year;
import bsbll.bases.BaseSituation;
import bsbll.game.play.EventType;
import bsbll.player.PlayerId;

public final class PerPlateAppearanceDistribution extends DefaultGameHandler {
    // TODO: Some events, such as WILD_PITCH, PASSED_BALL, and BALK, are only
    // applicable with runners on.
    private final EventType type;
    private final boolean requiresRunnersOn;
    private int plateAppearances;
    private final Multiset<Integer> distribution = HashMultiset.create();
    
    private PlayerId previousBatter;
    private int count;
    
    public PerPlateAppearanceDistribution(EventType type, boolean requiresRunnersOn) {
        this.type = requireNonNull(type);
        this.requiresRunnersOn = requiresRunnersOn;
    }

    @Override
    protected void process(ParsedPlay play, BaseSituation bases, int outs) {
        if (previousBatter == null || !play.getBatterId().equals(previousBatter)) {
            // This logic is not correct, since a plate appearance can span over
            // multiple innings (e.g. the last out is made on a caught stealing).
            if (!(requiresRunnersOn && bases.isEmpty())) {
                ++plateAppearances;
            }
            if (count > 0) {
                distribution.add(count);
            }
            previousBatter = play.getBatterId();
            count = 0;
        }
        if (play.getType() == type) {
            ++count;
        }
    }
 
    @Override
    public void afterInning() {
        if (count > 0) {
            distribution.add(count);
        }
    }

    public void report(Year year) {
        System.out.println(type + " in " + year);
        System.out.println("Plate Appearances: " + plateAppearances);
        for (Multiset.Entry<Integer> e : Multisets.copyHighestCountFirst(distribution).entrySet()) {
            System.out.println(String.format("%s: %s time(s)", e.getElement(), e.getCount()));
        }
    }
    
    public static void main(String[] args) {
        Year year = Year.of(1925);
        PerPlateAppearanceDistribution counter = new PerPlateAppearanceDistribution(EventType.PASSED_BALL, true);
        counter.parseAll(PlayByPlayFileUtils.getFolder(year));
        counter.report(year);
    }
}
