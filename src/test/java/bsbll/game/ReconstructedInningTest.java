package bsbll.game;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advance;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.game.RunsScored.Run;
import bsbll.game.params.GamePlayParams;
import bsbll.game.play.EventType;
import bsbll.game.play.Play;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.team.BattingOrder;

public final class ReconstructedInningTest {
    private final Player pitcher = new Player("pitcher", "Pitcher Doe");
    private final BattingOrder batters = buildBattingOrder();
    
    private static BattingOrder buildBattingOrder() {
        List<Player> batters = new ArrayList<>();
        for (int n = 1; n <= 9; ++n) {
            batters.add(new Player("batter-" + n, "Batter-" + n + " Doe"));
        }
        return BattingOrder.of(batters);
    }
    
    @Test
    public void singlePassedBallSingleThreeStrikeouts() {
        List<Play> plays = new ArrayList<>();
        plays.add(new Play(batters.nextBatter(), pitcher, 
                new PlayOutcome(EventType.SINGLE, Advances.of(Advance.safe(Base.HOME, Base.FIRST)))));
        Player secondBatter = batters.nextBatter();
        plays.add(new Play(secondBatter, pitcher, 
                new PlayOutcome(EventType.PASSED_BALL, Advances.of(Advance.safe(Base.FIRST, Base.SECOND)))));
        plays.add(new Play(secondBatter, pitcher, 
                new PlayOutcome(EventType.SINGLE, Advances.of(Advance.safe(Base.HOME, Base.FIRST), Advance.safe(Base.SECOND, Base.HOME)))));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        ReconstructedInning reconstructed = new ReconstructedInning(Inning.startOfGame(), plays, GamePlayParams.defaultParams());
        
        ImmutableList<Run> earnedRuns = reconstructed.getEarnedRuns();
        
        assertTrue(earnedRuns.isEmpty());
    }
}
