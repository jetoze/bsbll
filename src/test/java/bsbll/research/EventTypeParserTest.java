package bsbll.research;

import static bsbll.research.EventType.*;
import static bsbll.research.EventTypeParser.getBasicPlay;
import static bsbll.research.EventTypeParser.parse;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test for EventTypeParser.
 */
public final class EventTypeParserTest {

    @Test
    public void parseSingle() {
        assertSame(SINGLE, parse("S"));
        assertSame(SINGLE, parse("S#"));
        assertSame(SINGLE, parse("S?"));
        assertSame(SINGLE, parse("S7"));
        assertSame(SINGLE, parse("S7/G5.3-H;2-H;1-3"));
    }

    @Test
    public void parseDouble() {
        assertSame(DOUBLE, parse("D"));
        assertSame(DOUBLE, parse("D#"));
        assertSame(DOUBLE, parse("D7"));
        assertSame(DOUBLE, parse("D7/G5.3-H;2-H;1-H"));
        assertSame(DOUBLE, parse("DGR"));
        assertSame(DOUBLE, parse("DGR7"));
        assertSame(DOUBLE, parse("DGR.2-H"));
    }

    @Test
    public void parseTriple() {
        assertSame(TRIPLE, parse("T"));
        assertSame(TRIPLE, parse("T#"));
        assertSame(TRIPLE, parse("T7"));
        assertSame(TRIPLE, parse("T7/G5.3-H;2-H;1-3"));
    }

    @Test
    public void parseHomerun() {
        assertSame(HOMERUN, parse("H"));
        assertSame(HOMERUN, parse("H9"));
        assertSame(HOMERUN, parse("HR"));
        assertSame(HOMERUN, parse("HR9"));
        assertSame(HOMERUN, parse("H/L7D"));
        assertSame(HOMERUN, parse("H.1-H;B-H"));
        assertSame(HOMERUN, parse("HR.1-H;B-H"));
    }
    
    @Test
    public void parseHitByPitch() {
        assertSame(HIT_BY_PITCH, parse("HP"));
        assertSame(HIT_BY_PITCH, parse("HP.1-2"));
    }

    @Test
    public void parseStrikeout() {
        assertSame(STRIKEOUT, parse("K"));
        assertSame(STRIKEOUT, parse("K23"));
        assertSame(STRIKEOUT, parse("K13"));
        assertSame(STRIKEOUT, parse("K14"));
        assertSame(STRIKEOUT, parse("K2/C"));
        assertSame(STRIKEOUT, parse("K+PB.1-2"));
        assertSame(STRIKEOUT, parse("K23+PB.1-2"));
        assertSame(STRIKEOUT, parse("K13+PB.1-2"));
        assertSame(STRIKEOUT, parse("K14+PB.1-2"));
    }

    @Test
    public void parseWalk() {
        assertSame(WALK, parse("W"));
        assertSame(WALK, parse("W#"));
        assertSame(WALK, parse("I"));
        assertSame(WALK, parse("I#"));
        assertSame(WALK, parse("IW"));
        assertSame(WALK, parse("IW#"));
        assertSame(WALK, parse("W.1-2"));
        assertSame(WALK, parse("IW.1-2"));
        assertSame(WALK, parse("I.1-2"));
        assertSame(WALK, parse("W+WP.2-3"));
    }
    
    @Test
    public void parseFieldersChoice() {
        assertSame(FIELDERS_CHOICE, parse("FC"));
        assertSame(FIELDERS_CHOICE, parse("FC5"));
        assertSame(FIELDERS_CHOICE, parse("FC3/G3S.3-H;1-2"));
    }
    
    @Test
    public void parsePassedBall() {
        assertSame(PASSED_BALL, parse("PB"));
        assertSame(PASSED_BALL, parse("PB.2-3"));
    }
    
    @Test
    public void parseWildPitch() {
        assertSame(WILD_PITCH, parse("WP"));
        assertSame(WILD_PITCH, parse("WP.2-3"));
    }
    
    @Test
    public void parseReachedOnError() {
        assertSame(REACHED_ON_ERROR, parse("E2"));
        assertSame(REACHED_ON_ERROR, parse("E7.3-H"));
        assertSame(REACHED_ON_ERROR, parse("4E3"));
        assertSame(REACHED_ON_ERROR, parse("34E1#"));
    }
    
    @Test
    public void parseErrorOnFoulFly() {
        assertSame(ERROR_ON_FOUL_FLY, parse("FLE7"));
    }
    
    @Test
    public void parseInterference() {
        assertSame(INTERFERENCE, parse("C/E2.1-2"));
        assertSame(INTERFERENCE, parse("C/E1.1-2"));
        assertSame(INTERFERENCE, parse("C/E3.1-2"));
    }
    
    @Test
    public void parseOuts() {
        assertSame(OUT, parse("8"));
        assertSame(OUT, parse("8/F78"));
        assertSame(OUT, parse("63/G6M"));
        assertSame(OUT, parse("143/G1"));
        assertSame(OUT, parse("54(B)/BG25/SH.1-2"));
    }
    
    @Test
    public void parseBalk() {
        assertSame(BALK, parse("BK.3-H;1-2"));
    }
    
    @Test
    public void parseStolenBase() {
        assertSame(STOLEN_BASE, parse("SB2"));
        assertSame(STOLEN_BASE, parse("SB3;SB2"));
        assertSame(STOLEN_BASE, parse ("SBH;SB2"));
    }
    
    @Test
    public void parseCaughtStealing() {
        assertSame(CAUGHT_STEALING, parse("CSH(12)"));
        assertSame(CAUGHT_STEALING, parse("CS2(24).2-3"));
        assertSame(CAUGHT_STEALING, parse("CS2(2E4).1-3"));
    }
    
    @Test
    public void parsePickedOff() {
        assertSame(PICKED_OFF, parse("PO1(14)"));
        assertSame(PICKED_OFF, parse("PO2(E4)"));
        assertSame(PICKED_OFF, parse("PO3"));
        assertSame(PICKED_OFF, parse("POCS2"));
        assertSame(PICKED_OFF, parse("POCS3"));
        assertSame(PICKED_OFF, parse("POCSH"));
    }
    
    @Test
    public void parseDefensiveIndifference() {
        assertSame(DEFENSIVE_INDIFFERENCE, parse("DI"));
    }
    
    @Test
    public void parseNoPlay() {
        assertSame(NO_PLAY, parse("NP"));
    }
    
    @Test
    public void parseOtherAdvance() {
        assertSame(OTHER_ADVANCE, parse("OA"));
        assertSame(OTHER_ADVANCE, parse("OA.2X3(25)"));
    }

    @Test
    public void testGetBasicPlay() {
        assertEquals("8", getBasicPlay("8"));
        assertEquals("8", getBasicPlay("8/L8"));
        assertEquals("8", getBasicPlay("8.3-H"));
        assertEquals("8", getBasicPlay("8/L8.3-H"));
        assertEquals("S7", getBasicPlay("S7"));
    }
    
    @Test
    public void testForceOutAtSecond() {
        assertSame(FORCE_OUT, parse("54(1)/FO"));
    }
    
    @Test
    public void testInvalidInput() {
        Arrays.asList(
                "", 
                "S0",
                "D0",
                "T0",
                "SX",
                "DX",
                "TX",
                "X", 
                "It was a dark and stormy night",
                "K03",
                "Iwo",
                "HX",
                "C9",
                "Who's on first",
                "NPG",
                "BK6",
                "SB1",
                "SB4",
                "CS1",
                "CSY",
                "CS4",
                "POH",
                "PO4",
                "POCS1",
                "POCS4",
                "BKing",
                "DIX",
                "PB2",
                "WP3",
                "OAT",
                "KX").stream().forEach(this::invalidInputImpl);
    }
    
    private void invalidInputImpl(String field) {
        try {
            parse(field);
            fail("Expected an IllegalArgumentException for input \"" + field + "\"");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
