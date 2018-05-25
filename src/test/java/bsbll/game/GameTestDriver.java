package bsbll.game;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import bsbll.Year;
import bsbll.card.DieFactory;
import bsbll.card.LahmanPlayerCardLookup;
import bsbll.card.PlayerCardLookup;
import bsbll.game.HalfInning.Stats;
import bsbll.game.LineScore.Line;
import bsbll.game.report.LineScorePlainTextReport;
import bsbll.league.League;
import bsbll.league.LeagueId;
import bsbll.league.Standings;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.team.Record;
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

        playSeries(cardLookup, yankees, redSox, 22);
    }
    
    public static void playSeries(PlayerCardLookup cardLookup, Team yankees, Team redSox, int numberOfGames) {
        League league = new League(LeagueId.AL, yankees, redSox);
        List<LineScore> scores = new ArrayList<>();
        for (int n = 0; n < numberOfGames; ++n) {
            Game game = new Game(yankees, redSox, new Log5BasedMatchupRunner(cardLookup, DieFactory.random()));
            LineScore score = game.run();
            print(score);
            scores.add(score);
        }
        league.addGames(scores);
        print(league.getStandings());
    }
    
    private static void print(Standings standings) {
        System.out.println();
        System.out.println("Standings:");
        Padding namePad = Padding.of(12);
        Padding valuePad = Padding.of(4);
        String header = namePad.padRight("") + valuePad.padLeft("W") + valuePad.padLeft("L") +
                "   " + valuePad.padLeft("R") + valuePad.padLeft("RA");
        System.out.println(header);
        for (Team team : standings.sortByWinPct()) {
            Record record = standings.getRecord(team);
            String line = namePad.padRight(team.getName().getMainName()) +
                    valuePad.padLeft(record.getWins()) + valuePad.padLeft(record.getLosses()) +
                    "   " + valuePad.padLeft(record.getRunsScored()) + valuePad.padLeft(record.getRunsAgainst());
            System.out.println(line);
        }
    }
    
    public static void playUntilDoubleDigitInning(PlayerCardLookup cardLookup, Team yankees, Team redSox) {
        playUntil(cardLookup, yankees, redSox, GameTestDriver::hasDoubleDigitInning);
    }
    
    private static boolean hasDoubleDigitInning(LineScore score) {
        return hasDoubleDigitInning(score.getVisitingLine()) ||
                hasDoubleDigitInning(score.getHomeLine());
    }
    
    private static boolean hasDoubleDigitInning(Line line) {
        return line.getInnings().stream()
                .mapToInt(Stats::getRuns)
                .anyMatch(i -> i >= 10);
    }

    public static void playUntilNoHitter(PlayerCardLookup cardLookup, Team yankees, Team redSox) {
        playUntil(cardLookup, yankees, redSox, LineScore::isNoHitter);
    }
    
    public static void playUntilTwentyInningGame(PlayerCardLookup cardLookup, Team yankees, Team redSox) {
        playUntil(cardLookup, yankees, redSox, sc -> sc.getVisitingLine().getInnings().size() >= 20);
    }
    
    public static void playUntilTwentyRunMargin(PlayerCardLookup cardLookup, Team yankees, Team redSox) {
        playUntil(cardLookup, yankees, redSox, sc -> 
            Math.abs(sc.getHomeLine().getRuns() - sc.getVisitingLine().getRuns()) >= 20);
    }
    
    private static void playUntil(PlayerCardLookup cardLookup, 
                                  Team yankees, 
                                  Team redSox, 
                                  Predicate<LineScore> condition) {
        for (int n = 1; n <= 100_000; ++n) {
            if ((n % 1000) == 0) {
                System.out.println(n);
            }
            Game game = new Game(yankees, redSox, new Log5BasedMatchupRunner(cardLookup, DieFactory.random()));
            LineScore score = game.run();
            if (condition.test(score)) {
                print(score);
                System.out.println(n);
                break;
            }
        }
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
        LineScorePlainTextReport report = new LineScorePlainTextReport(TeamName.Mode.ABBREV);
        report.writeTo(score, System.out);
        System.out.println();
    }
}
