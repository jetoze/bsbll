package bsbll.stats;

import static bsbll.stats.PitchingStat.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PitchingStatLineTest {
    public static final PitchingStatLine gregMaddux1995 = PitchingStatLine.builder()
            .set(GAMES, 28)
            .set(GAMES_STARTED, 28)
            .set(COMPLETE_GAMES, 10)
            .set(SHUTOUTS, 3)
            .set(WINS, 19)
            .set(LOSSES, 2)
            .set(BATTERS_FACED, 785)
            .set(OUTS, 629)
            .set(HITS, 147)
            .set(EARNED_RUNS, 38)
            .set(HOMERUNS, 8)
            .set(WALKS, 23)
            .set(STRIKEOUTS, 181)
            .set(HIT_BY_PITCHES, 4)
            .build();
    
    @Test
    public void whip() {
        assertEquals("0.811", gregMaddux1995.get(WHIP).toString());
    }
    
    @Test
    public void era() {
        assertEquals("1.63", gregMaddux1995.get(ERA).toString());
    }
    
    @Test
    public void winPct() {
        assertEquals(".905", gregMaddux1995.get(WIN_PCT).toString());
    }
}
