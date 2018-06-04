package bsbll.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import bsbll.game.Game.GameStats;
import bsbll.game.HalfInning.Stats;
import bsbll.matchup.MatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.team.BattingOrder;

/**
 * Unit tests for HalfInning.
 */
public final class HalfInningTest {
    private final BattingOrder battingOrder = createBattingOrder();
    private final Player pitcher = new Player(PlayerId.of("Pitcher 1"));
    
    @Test
    public void threeUpThreeDown() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.OUT, Outcome.STRIKEOUT, Outcome.OUT);
        HalfInning halfInning = new HalfInning(battingOrder, pitcher, mr, new GameStats(), 0);
        
        Stats stats = halfInning.run();
        
        assertEquals(new Stats(0, 0, 0, 3, 0), stats);
    }
    
    @Test
    public void singleWalkStrikeoutHomerunDoubleOutOut() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.SINGLE, Outcome.WALK, 
                Outcome.STRIKEOUT, Outcome.HOMERUN, Outcome.DOUBLE, Outcome.OUT, Outcome.OUT);
        HalfInning halfInning = new HalfInning(battingOrder, pitcher, mr, new GameStats(), 0);
        
        Stats stats = halfInning.run();
        
        assertEquals(new Stats(3, 3, 0, 3, 1), stats);
    }
    
    @Test
    public void walkOffHitByPitch() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.SINGLE, Outcome.SINGLE, Outcome.WALK, Outcome.WALK);
        int runsNeededToWin = 1;
        HalfInning halfInning = new HalfInning(battingOrder, pitcher, mr, new GameStats(), runsNeededToWin);
        
        Stats stats = halfInning.run();
        
        assertEquals(new Stats(1, 2, 0, 0, 3), stats);
    }
    
    
    private static BattingOrder createBattingOrder() {
        List<Player> batters = new ArrayList<>();
        for (int n = 1; n <= 9; ++n) {
            batters.add(new Player(PlayerId.of("Batter " + n)));
        }
        return new BattingOrder(batters);
    }
    
    private static final class StaticMatchupRunner implements MatchupRunner {
        private final ImmutableList<Outcome> outcomes;
        private int index;
        
        public StaticMatchupRunner(Outcome... outcomes) {
            this.outcomes = ImmutableList.copyOf(outcomes);
        }

        @Override
        public Outcome run(Player batter, Player pitcher) {
            assertTrue(index < outcomes.size());
            Outcome o = outcomes.get(index);
            ++index;
            return o;
        }
    }
}
