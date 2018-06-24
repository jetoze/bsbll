package bsbll.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Base;
import bsbll.bases.OccupiedBases;
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
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .build()));
        Player secondBatter = batters.nextBatter();
        plays.add(new Play(secondBatter, pitcher, PlayOutcome.builder(EventType.PASSED_BALL)
                .withSafeAdvance(Base.FIRST, Base.SECOND).build()));
        plays.add(new Play(secondBatter, pitcher, PlayOutcome.builder(EventType.SINGLE)
                .withSafeAdvance(Base.HOME, Base.FIRST)
                .withSafeAdvance(Base.SECOND, Base.HOME)
                .build()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        ReconstructedInning reconstructed = new ReconstructedInning(Inning.startOfGame(), plays, GamePlayParams.defaultParams());
        
        ImmutableList<Run> earnedRuns = reconstructed.getEarnedRuns();
        
        assertTrue(earnedRuns.isEmpty());
    }
    
    @Test
    public void errorHomerunThreeStrikeouts() {
        Inning inning = Inning.startOfGame();
        List<Play> plays = new ArrayList<>();
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.builder(EventType.OUT)
                .withSafeOnError(Base.HOME, Base.FIRST)
                .withErrors(1)
                .build()));
        Player homerunHitter = batters.nextBatter();
        plays.add(new Play(homerunHitter, pitcher, PlayOutcome.homerun(OccupiedBases.FIRST)));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        plays.add(new Play(batters.nextBatter(), pitcher, PlayOutcome.strikeout()));
        ReconstructedInning reconstructed = new ReconstructedInning(inning, plays, GamePlayParams.defaultParams());
        
        ImmutableList<Run> earnedRuns = reconstructed.getEarnedRuns();
        
        Run expectedEarnedRun = new Run(inning, new BaseRunner(homerunHitter, pitcher));
        assertEquals(ImmutableList.of(expectedEarnedRun), earnedRuns);
    }
}
