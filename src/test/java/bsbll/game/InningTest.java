package bsbll.game;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

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
    
}
