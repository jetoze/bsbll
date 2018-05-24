package bsbll.card;

import bsbll.Year;
import bsbll.lahman.BattingFileExplorer;
import bsbll.lahman.PitchingFileExplorer;
import bsbll.league.LeagueId;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStats;

public final class PlayerCardExperiments {

    public static void main(String[] args) {
        // How does 1923 Babe Ruth do against a league-average pitcher?
        PlayerCard leagueCard = americanLeague1923();
        PlayerCard batterCard = babeRuth1923();
        PlayerCard pitcherCard = walterJohnson1923(leagueCard);
        PlayerCardLookup cardLookup = new PlayerCardLookup() {
            @Override
            public PlayerCard getPitchingCard(Player player) {
                return pitcherCard;
            }
            
            @Override
            public PlayerCard getLeagueCard(LeagueId leagueId) {
                return leagueCard;
            }
            
            @Override
            public PlayerCard getBattingCard(Player player) {
                return batterCard;
            }
        };
        Log5BasedMatchupRunner matchup = new Log5BasedMatchupRunner(leagueCard, cardLookup, DieFactory.random());
        
        Player batter = new Player(PlayerId.of("ruthba01"));
        Player pitcher = new Player(PlayerId.of("johnwa01"));
        BattingStats stats = new BattingStats();
        for (int n = 0; n < 697; ++n) {
            Outcome outcome = matchup.run(batter, pitcher);
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
