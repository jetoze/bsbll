package bsbll.stats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bsbll.stats.PitchingStat.PrimitivePitchingStat;

public final class PitchingStatLineTest {
    public static final PitchingStatLine foo = PitchingStatLine.builder()
            .set(PrimitivePitchingStat.BATTERS_FACED, 10)
            .build();
    @Test
    public void whip() {
        PitchingStatLine stats = PitchingStatLine.builder()
                .set(PrimitivePitchingStat.OUTS, 60)
                .set(PrimitivePitchingStat.HITS, 17)
                .set(PrimitivePitchingStat.WALKS, 4)
                .build();
        
        Average whip = stats.get(PitchingStat.WHIP);
        
        assertEquals("1.050", whip.toString());
    }
}
