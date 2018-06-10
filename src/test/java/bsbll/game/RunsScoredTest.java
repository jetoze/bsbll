package bsbll.game;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bsbll.game.RunsScored.Run;
import bsbll.player.Player;

public final class RunsScoredTest {

    @Test
    public void losingPitcher() {
        Player homePitcher1 = new Player("p1", "P1 Doe");
        Player visitingPitcher1 = new Player("p2", "P2 Doe");
        Player visitingPitcher2 = new Player("p3", "P3 Doe");
        Player visitingPitcher3 = new Player("p4", "P4 Doe");
        List<Run> runs = new ArrayList<>();
        Inning topOfFirst = Inning.startOfGame();
        runs.add(new Run(topOfFirst, new Player("r1", "R1 Doe"), homePitcher1)); // 1-0
        runs.add(new Run(topOfFirst, new Player("r2", "R2 Doe"), homePitcher1)); // 2-0
        Inning bottomOfThird = Inning.bottomOf(3);
        runs.add(new Run(bottomOfThird, new Player("r3", "R3 Doe"), visitingPitcher1)); // 2-1
        runs.add(new Run(bottomOfThird, new Player("r4", "R4 Doe"), visitingPitcher1)); // 2-2
        Inning bottomOfFifth = Inning.bottomOf(5);
        runs.add(new Run(bottomOfFifth, new Player("r5", "R5 Doe"), visitingPitcher2)); // 2-3
        Inning topOfSixth = Inning.topOf(6);
        runs.add(new Run(topOfSixth, new Player("r6", "R6 Doe"), homePitcher1)); // 3-3
        Inning bottomOfSeventh = Inning.bottomOf(7);
        runs.add(new Run(bottomOfSeventh, new Player("r7", "R7 Doe"), visitingPitcher2)); // 3-4
        runs.add(new Run(bottomOfSeventh, new Player("r8", "R8 Doe"), visitingPitcher3)); // 3-5
        
        Player lp = RunsScored.of(runs).getLosingPitcher();
        
        assertSame(visitingPitcher2, lp);
    }

}
