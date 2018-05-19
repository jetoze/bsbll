package bsbll.card;

import bsbll.matchup.Matchup;
import bsbll.matchup.Matchup.Outcome;
import bsbll.stats.BattingStats;

public final class PlayerCardExperiments {

    public static void main(String[] args) {
        // How does 1923 Babe Ruth do against a league-average pitcher?
        PlayerCard league = americanLeague1923();
        PlayerCard batter = babeRuth1923();
        PlayerCard pitcher = curtFullerton1923(league);
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
        return PlayerCard.builder(48165)
                .hits(11870)
                .doubles(2010)
                .triples(554)
                .homeruns(442)
                .walks(4069)
                .strikeouts(3613)
                .hitByPitches(341)
                .build();
        
    }
    
    private static PlayerCard babeRuth1923() {
        return PlayerCard.builder(697)
                .hits(205)
                .doubles(45)
                .triples(13)
                .homeruns(41)
                .walks(170)
                .strikeouts(93)
                .hitByPitches(4)
                .build();
    }
  
    private static PlayerCard walterJohnson1923(PlayerCard league) {
        return PlayerCard.builder(1096)
                .hits(263)
                .doubles(league.homeruns())
                .triples(league.triples())
                .homeruns(9)
                .walks(73)
                .strikeouts(130)
                .hitByPitches(20)
                .build();
    }
    
    private static PlayerCard curtFullerton1923(PlayerCard league) {
        return PlayerCard.builder(657)
                .hits(167)
                .doubles(league.doubles())
                .triples(league.triples())
                .homeruns(10)
                .walks(71)
                .strikeouts(37)
                .hitByPitches(6)
                .build();
    }
    
}
