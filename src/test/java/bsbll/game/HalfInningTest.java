package bsbll.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import bsbll.die.DieFactory;
import bsbll.game.HalfInning.Stats;
import bsbll.game.event.GameEventDetector;
import bsbll.game.params.BaseHitAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceDistribution;
import bsbll.game.params.ErrorCountDistribution;
import bsbll.game.params.FieldersChoiceProbabilities;
import bsbll.game.params.OutAdvanceDistribution;
import bsbll.matchup.MatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.team.BattingOrder;

/**
 * Unit tests for HalfInning.
 */
public final class HalfInningTest {
    private final BattingOrder battingOrder = createBattingOrder();
    private final Player pitcher = new Player("Pitcher 1", "John Doe");
    
    @Test
    public void threeUpThreeDown() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.OUT, Outcome.STRIKEOUT, Outcome.OUT);
        HalfInning halfInning = new HalfInning(Inning.startOfGame(), battingOrder, pitcher, 
                gamePlayDriver(mr), new PlayerGameStats(), GameEventDetector.NO_EVENTS, 0);
        
        Stats stats = halfInning.run().getStats();
        
        assertEquals(new Stats(0, 0, 0, 3, 0), stats);
    }
    
    private static GamePlayDriver gamePlayDriver(MatchupRunner mr) {
        return new GamePlayDriver(
                mr, 
                BaseHitAdvanceDistribution.defaultAdvances(), 
                OutAdvanceDistribution.defaultAdvances(),
                FieldersChoiceProbabilities.defaultValues(),
                ErrorCountDistribution.noErrors(),
                ErrorAdvanceDistribution.defaultAdvances(),
                DieFactory.random());
    }
    
    @Test
    public void singleWalkStrikeoutHomerunDoubleOutOut() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.SINGLE, Outcome.WALK, 
                Outcome.STRIKEOUT, Outcome.HOMERUN, Outcome.DOUBLE, Outcome.OUT, Outcome.OUT);
        HalfInning halfInning = new HalfInning(Inning.startOfGame(), battingOrder, pitcher, 
                gamePlayDriver(mr), new PlayerGameStats(), GameEventDetector.NO_EVENTS, 0);
        
        Stats stats = halfInning.run().getStats();
        
        assertEquals(new Stats(3, 3, 0, 3, 1), stats);
    }
    
    @Test
    public void walkOffHitByPitch() {
        MatchupRunner mr = new StaticMatchupRunner(Outcome.SINGLE, Outcome.SINGLE, Outcome.WALK, Outcome.WALK);
        int runsNeededToWin = 1;
        HalfInning halfInning = new HalfInning(Inning.startOfGame(), battingOrder, pitcher, 
                gamePlayDriver(mr), new PlayerGameStats(), GameEventDetector.NO_EVENTS, runsNeededToWin);
        
        Stats stats = halfInning.run().getStats();
        
        assertEquals(new Stats(1, 2, 0, 0, 3), stats);
    }
    
    
    private static BattingOrder createBattingOrder() {
        List<Player> batters = new ArrayList<>();
        for (int n = 1; n <= 9; ++n) {
            batters.add(new Player("Batter " + n, "John Doe"));
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
