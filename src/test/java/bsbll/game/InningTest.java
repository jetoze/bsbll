package bsbll.game;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import bsbll.game.Inning.Half;

public final class InningTest {

    @Test
    public void testSorting() {
        List<Inning> innings = Arrays.asList(Inning.bottomOf(1), Inning.bottomOf(2),
                Inning.topOf(1), Inning.topOf(3));
        
        Collections.sort(innings);
        
        List<Inning> expected = Arrays.asList(Inning.topOf(1), Inning.bottomOf(1), 
                Inning.bottomOf(2), Inning.topOf(3));
        assertEquals(expected, innings);
    }
    
    @Test
    public void testTopOf() {
        int length = 14;
        for (int num = 1; num <= length; ++num) {
            Inning inning = Inning.topOf(num);
            assertEquals(num, inning.getNumber());
            assertSame(Half.TOP, inning.getHalf());
            assertTrue(inning.isTop());
        }
    }
    
    @Test
    public void testBottomOf() {
        int length = 14;
        for (int num = 1; num <= length; ++num) {
            Inning inning = Inning.bottomOf(num);
            assertEquals(num, inning.getNumber());
            assertSame(Half.BOTTOM, inning.getHalf());
            assertTrue(inning.isBottom());
        }
    }
}
