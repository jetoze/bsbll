package bsbll.game;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import bsbll.Year;
import bsbll.card.DieFactory;
import bsbll.card.LahmanPlayerCardLookup;
import bsbll.card.PlayerCardLookup;
import bsbll.game.report.LineScoreSummaryPlainTextReport;
import bsbll.game.report.LineScoreSummaryPlainTextReport.TeamNameMode;
import bsbll.league.LeagueId;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.team.Roster;
import bsbll.team.Team;
import bsbll.team.TeamId;
import bsbll.team.TeamName;

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

        for (int n = 0; n < 10; ++n) {
            Game game = new Game(yankees, redSox, new Log5BasedMatchupRunner(cardLookup, DieFactory.random()));
            LineScore score = game.run();
            print(score);
        }
    }

    public static void playUntilNoHitter(PlayerCardLookup cardLookup, Team yankees, Team redSox) {
        LineScore score = null;
        int games = 0;
        while (true) {
            Game game = new Game(yankees, redSox, new Log5BasedMatchupRunner(cardLookup, DieFactory.random()));
            score = game.run();
            ++games;
            if (score.isNoHitter()) {
                break;
            }
            if ((games % 100) == 0) {
                System.out.println(games);
            }
        }
        
        print(score);
        System.out.println(games);
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
                "piercbi01"
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
        LineScoreSummaryPlainTextReport report = new LineScoreSummaryPlainTextReport(TeamNameMode.ABBREV);
        report.writeTo(score, System.out);
        System.out.println();
    }
}
