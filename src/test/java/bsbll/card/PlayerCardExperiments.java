package bsbll.card;

import static bsbll.stats.BattingStat.*;

import bsbll.Year;
import bsbll.league.LeagueId;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingStatLine;

public final class PlayerCardExperiments {

    public static void main(String[] args) {
        // How does 1923 Babe Ruth do against various pitchers?
        PlayerCardLookup cardLookup = new LahmanPlayerCardLookup(LeagueId.AL, Year.of(1923));
        
        Log5BasedMatchupRunner matchup = new Log5BasedMatchupRunner(cardLookup, DieFactory.random());
        
        Player batter = new Player(PlayerId.of("ruthba01"));
        Player pitcher = new Player(PlayerId.of("johnswa01"));
        BattingStatLine stats = new BattingStatLine();
        for (int n = 0; n < 697; ++n) {
            Outcome outcome = matchup.run(batter, pitcher);
            stats = stats.add(outcome);
        }
        System.out.println("PA: " + stats.get(PLATE_APPEARANCES));
        System.out.println("AB: " + stats.get(AT_BATS));
        System.out.println("H : " + stats.get(HITS));
        System.out.println("2B: " + stats.get(DOUBLES));
        System.out.println("3B: " + stats.get(TRIPLES));
        System.out.println("HR: " + stats.get(HOMERUNS));
        System.out.println("BB: " + stats.get(WALKS));
        System.out.println("SO: " + stats.get(STRIKEOUTS));
        System.out.println("BA: " + stats.get(BATTING_AVERAGE));
        System.out.println("SA: " + stats.get(SLUGGING_PERCENTAGE));
        System.out.println("HBP: " + stats.get(HIT_BY_PITCHES));
    }
}
