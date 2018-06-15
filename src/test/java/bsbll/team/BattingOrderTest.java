package bsbll.team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bsbll.player.Player;

public final class BattingOrderTest {
    private List<Player> batters;
    private BattingOrder bo;

    @Before
    public void setup() {
        batters = new ArrayList<>();
        for (int n = 0; n < 9; ++n) {
            batters.add(new Player("p-" + n, "John-" + n + " Doe"));
        }
        bo = BattingOrder.of(batters);
    }
    
    @Test
    public void twoTimesThroughTheOrder() {
        List<Player> actual = new ArrayList<>();
        for (int n = 0; n < 18; ++n) {
            actual.add(bo.nextBatter());
        }
        
        List<Player> expected = new ArrayList<>(batters);
        expected.addAll(batters);
        assertEquals(expected, actual);
    }
    
    @Test
    public void returnFirstBatter() {
        Player b1 = bo.nextBatter();
        
        bo.returnBatter(b1);
        Player b2 = bo.nextBatter();
        
        assertSame(b1, b2);
    }
    
    @Test
    public void returnLastBatter() {
        Player b1 = null;
        for (int n = 0; n < 9; ++n) {
            b1 = bo.nextBatter();
        }
        
        bo.returnBatter(b1);
        Player b2 = bo.nextBatter();
        
        assertSame(b1, b2);
    }
}
