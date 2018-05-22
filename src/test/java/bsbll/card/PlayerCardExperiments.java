package bsbll.card;

import bsbll.Year;
import bsbll.lahman.BattingFileExplorer;
import bsbll.lahman.PitchingFileExplorer;
import bsbll.league.LeagueId;
import bsbll.matchup.Matchup;
import bsbll.matchup.Matchup.Outcome;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStats;

public final class PlayerCardExperiments {

    public static void main(String[] args) {
        // How does 1923 Babe Ruth do against a league-average pitcher?
        PlayerCard league = americanLeague1923();
        PlayerCard batter = babeRuth1923();
        PlayerCard pitcher = walterJohnson1923(league);
        Matchup matchup = new Matchup(batter, pitcher, league);
        DieFactory dieFactory = DieFactory.random();
        
        BattingStats stats = new BattingStats();
        for (int n = 0; n < 697; ++n) {
            Outcome outcome = matchup.run(dieFactory);
            stats = stats.add(outcome);
        }
        System.out.println("PA: " + stats.getPlateAppearances());
        System.out.println("AB: " + stats.getAtBats());
        System.out.println("H : " + stats.getHits());
        System.out.println("S : " + stats.getSingles());
        System.out.println("2B: " + stats.getDoubles());
        System.out.println("3B: " + stats.getTriples());
        System.out.println("HR: " + stats.getHomeruns());
        System.out.println("BB: " + stats.getWalks());
        System.out.println("SO: " + stats.getStrikeouts());
        System.out.println("BA: " + stats.getBattingAverage());
        System.out.println("SA: " + stats.getSluggingPercentage());
        System.out.println("HBP: " + stats.getHitByPitches());
    }
    
    private static PlayerCard americanLeague1923() {
        return BattingFileExplorer.defaultExplorer()
                .generateLeagueCard(LeagueId.AL, Year.of(1923));
        
    }
    
    private static PlayerCard babeRuth1923() {
        return BattingFileExplorer.defaultExplorer()
                .generatePlayerCard(new PlayerId("ruthba01"), Year.of(1923));
    }
  
    private static PlayerCard walterJohnson1923(PlayerCard league) {
        return PitchingFileExplorer.defaultExplorer()
                .generatePlayerCard(new PlayerId("johnswa01"), Year.of(1923), league);
    }
    
    private static PlayerCard curtFullerton1923(PlayerCard league) {
        return PitchingFileExplorer.defaultExplorer()
                .generatePlayerCard(new PlayerId("fullecu01"), Year.of(1923), league);
    }
    
}
