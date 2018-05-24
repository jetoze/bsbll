package bsbll.game;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import bsbll.Year;
import bsbll.card.DieFactory;
import bsbll.card.LahmanPlayerCardLookup;
import bsbll.card.PlayerCardLookup;
import bsbll.game.LineScore.Line;
import bsbll.league.LeagueId;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.team.Roster;
import bsbll.team.Team;
import bsbll.team.TeamId;
import bsbll.team.TeamName;
import tzeth.strings.Padding;

public final class GameTestDriver {

    public GameTestDriver() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        LeagueId leagueId = LeagueId.AL;
        Year year = Year.of(1923);
        PlayerCardLookup cardLookup = new LahmanPlayerCardLookup(leagueId, year);
        Team yankees = createYankees();
        Team redSox = createRedSox();
        
        Game game = new Game(yankees, redSox, new Log5BasedMatchupRunner(cardLookup, DieFactory.random()));
        LineScore score = game.run();
        
        print(score);
    }

    private static Team createYankees() {
        TeamId id = new TeamId("NYY");
        TeamName name = new TeamName("New York", "Yankees", "NYY");
        List<Player> batters = createPlayers(
                "pippwa01",
                "wardaa01",
                "meusebo01",
                "ruthba01",
                "wittwh01",
                "duganjo01",
                "scottev01",
                "schanwa01"
        );
        List<Player> pitchers = createPlayers(
                "shawkbo01",
                "bushjo01",
                "hoytwa01",
                "pennohe01",
                "jonessa01"
        );
        Roster roster = new Roster(batters, pitchers);
        return new Team(id, name, roster);
    }

    private static Team createRedSox() {
        TeamId id = new TeamId("BOA");
        TeamName name = new TeamName("Boston", "Red Sox", "BOS");
        List<Player> batters = createPlayers(
                "picinva01",
                "flagsir01",
                "burnsge02",
                "harrijo03",
                "shankho01",
                "fewstch01",
                "mitchjo01",
                "reichdi01"
        );
        List<Player> pitchers = createPlayers(
                "ehmkeho01",
                "quinnja01",
                "fergual01",
                "piercebi01"
        );
        Roster roster = new Roster(batters, pitchers);
        return new Team(id, name, roster);
    }
    
    private static List<Player> createPlayers(String... ids) {
        return Arrays.stream(ids)
                .map(id -> new Player(PlayerId.of(id)))
                .collect(toList());
    }
    
    private static void print(LineScore score) {
        Padding namePad = Padding.of(6);
        Padding valuePad = Padding.of(3);
        String header = namePad.padRight("") + valuePad.padLeft("R") + valuePad.padLeft("H") + valuePad.padLeft("R");
        String visitor = toString(score.getVisitingLine(), namePad, valuePad);
        String home = toString(score.getHomeLine(), namePad, valuePad);
        System.out.println(header);
        System.out.println(visitor);
        System.out.println(home);
    }
    
    private static String toString(Line line, Padding namePad, Padding valuePad) {
        return namePad.padRight(line.getTeam().getAbbreviation()) +
                valuePad.padLeft(line.getSummary().getRuns()) +
                valuePad.padLeft(line.getSummary().getHits()) +
                valuePad.padLeft(line.getSummary().getErrors());
    }
    
    
}
