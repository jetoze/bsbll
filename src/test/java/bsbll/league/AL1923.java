package bsbll.league;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.Year;
import bsbll.card.DieFactory;
import bsbll.card.LahmanPlayerCardLookup;
import bsbll.game.BoxScore;
import bsbll.game.DefaultGameEventDetector;
import bsbll.game.Game;
import bsbll.game.GameEventDetector;
import bsbll.game.LineScore;
import bsbll.game.report.BoxScorePlainTextReport;
import bsbll.game.report.LineScorePlainTextReport;
import bsbll.league.report.StandingsPlainTextReport;
import bsbll.matchup.Log5BasedMatchupRunner;
import bsbll.matchup.MatchupRunner;
import bsbll.player.Player;
import bsbll.player.PlayerFactory;
import bsbll.stats.Average;
import bsbll.stats.BattingLeaders;
import bsbll.stats.BattingStat;
import bsbll.team.Team;
import bsbll.team.TeamBuilder;
import bsbll.team.TeamId;
import tzeth.strings.Padding;

public final class AL1923 {
    private final League league;
    private final MatchupRunner matchupRunner;
    private final Random random = new Random();
    
    public AL1923() {
        league = new League(LeagueId.AL, Year.of(1923), 
                buildYankees(), 
                buildRedSox(), 
                buildTigers(), 
                buildIndians(),
                buildSenators(),
                buildBrowns(),
                buildAthletics(),
                buildWhiteSox());
        matchupRunner = new Log5BasedMatchupRunner(
                new LahmanPlayerCardLookup(LeagueId.AL, Year.of(1923)), 
                DieFactory.random());
        checkState(league.getNumberOfTeams() % 2 == 0);
    }
    
    public Standings run() {
        List<BoxScore> scores = new ArrayList<>();
        List<Team> teams = new ArrayList<>(league.getTeams());
        Collections.shuffle(teams);
        for (int a = 0; a < teams.size(); ++a) {
            for (int b = a + 1; b < teams.size(); ++b) {
                Team teamA = teams.get(a);
                Team teamB = teams.get(b);
                scores.addAll(runSeries(teamA, teamB, 11));
                scores.addAll(runSeries(teamB, teamA, 11));
            }
        }
        return league.getStandings();
    }
    
    public void runRoundsOfRandomSeries(int numberOfRounds) {
        List<Team> teams = new ArrayList<>(league.getTeams());
        for (int n = 0; n < numberOfRounds; ++n) {
            List<BoxScore> scores = new ArrayList<>();
            Collections.shuffle(teams);
            for (int t = 0; t < teams.size(); t += 2) {
                Team home = teams.get(t);
                Team visiting = teams.get(t + 1);
                int numberOfGames = random.nextInt(4) + 1;
                scores.addAll(runSeries(home, visiting, numberOfGames));
            }
            league.addBoxScores(scores);
            Standings standings = league.getStandings();
            print(standings, n + 1);
        }
    }
    
    private List<BoxScore> runSeries(Team home, Team visiting, int numberOfGames) {
        List<BoxScore> scores = new ArrayList<>();
        for (int n = 0; n < numberOfGames; ++n) {
            BoxScore score = runGame(home, visiting);
            league.addBoxScores(score);
            scores.add(score);
        }
        return scores;
    }
    
    private BoxScore runGame(Team home, Team visiting) {
        Game game = new Game(home, visiting, matchupRunner);
        GameEventDetector eventDetector = new DefaultGameEventDetector(league.getPlayerStatLookup());
        game.setGameEventDetector(eventDetector);
        BoxScore boxScore = game.run();
        return boxScore;
    }
    
    @SuppressWarnings("unused")
    private static void print(LineScore score) {
        LineScorePlainTextReport report = new LineScorePlainTextReport(NameMode.ABBREV);
        report.writeTo(score, System.out);
        System.out.println();
    }
    
    @SuppressWarnings("unused")
    private static void print(BoxScore boxScore) {
        BoxScorePlainTextReport report = new BoxScorePlainTextReport();
        report.writeTo(boxScore, System.out);
        System.out.println();
    }
    
    private static void print(Standings standings, int round) {
        System.out.println("Standings after Round " + round + ":");
        print(standings);
    }

    public static void print(Standings standings) {
        StandingsPlainTextReport report = new StandingsPlainTextReport(NameMode.MAIN);
        report.writeTo(standings, System.out);
        System.out.println();
    }

    private static Team buildYankees() {
        return TeamBuilder.newBuilder(TeamId.of("NYY"))
                .withMainName("New York")
                .withNickname("Yankees")
                .withAbbreviation("NYY")
                .withBatters(
                        "pippwa01",
                         "wardaa01",
                         "meusebo01",
                         "ruthba01",
                         "wittwh01",
                         "duganjo01",
                         "scottev01",
                         "schanwa01")
                .withPitchers(
                        "shawkbo01",
                         "bushjo01",
                         "hoytwa01",
                         "pennohe01",
                         "jonessa01")
                .build();
    }

    private static Team buildRedSox() {
        return TeamBuilder.newBuilder(TeamId.of("BOA"))
                .withMainName("Boston")
                .withNickname("Red Sox")
                .withAbbreviation("BOS")
                .withBatters(
                        "picinva01",
                        "flagsir01",
                        "burnsge02",
                        "harrijo03",
                        "shankho01",
                        "fewstch01",
                        "mitchjo01",
                        "reichdi01")
                .withPitchers(
                        "ehmkeho01",
                        "quinnja01",
                        "fergual01",
                        "piercbi01")
                .build();
    }

    private static Team buildTigers() {
        return TeamBuilder.newBuilder(TeamId.of("DET"))
                .withMainName("Detroit")
                .withNickname("Tigers")
                .withAbbreviation("DET")
                .withBatters(
                        "manushe01",
                        "rigneto01",
                        "cobbty01",
                        "heilmha01",
                        "veachbo01",
                        "bluelu01",
                        "bassljo01",
                        "haneyfr01")
                .withPitchers(
                        "daussho01",
                        "pillehe01",
                        "holloke01",
                        "colliri01")
                .build();
    }
    
    private static Team buildIndians() {
        return TeamBuilder.newBuilder(TeamId.of("CLE"))
                .withMainName("Cleveland")
                .withNickname("Indians")
                .withAbbreviation("CLE")
                .withBatters(
                        "jamiech01",
                        "browefr01",
                        "seweljo01",
                        "speaktr01",
                        "summaho01",
                        "wambsbi01",
                        "lutzkru01",
                        "oneilst01")
                .withPitchers(
                        "uhlege01",
                        "covelst01",
                        "edwarji01",
                        "smithsh01")
                .build();
    }
    
    private static Team buildSenators() {
        return TeamBuilder.newBuilder(TeamId.of("WAS"))
                .withMainName("Washington")
                .withNickname("Senators")
                .withAbbreviation("WAS")
                .withBatters(
                        "ruelmu01",
                        "gosligo01",
                        "judgejo01",
                        "ricesa01",
                        "leibone01",
                        "harribu01",
                        "bluegos01",
                        "peckiro01")
                .withPitchers(
                        "johnswa01",
                        "mogrige01",
                        "zachato01",
                        "zahnipa01")
                .build();
    }
    
    private static Team buildBrowns() {
        return TeamBuilder.newBuilder(TeamId.of("SLA"))
                .withMainName("St. Louis")
                .withNickname("Browns")
                .withAbbreviation("STL")
                .withBatters(
                        "severha01",
                        "tobinja01",
                        "mcmanma01",
                        "willike01",
                        "jacobba01",
                        "schlidu01",
                        "gerbewa01",
                        "ezzelho01")
                .withPitchers(
                        "vangiel01",
                        "shockur01",
                        "danfoda01",
                        "davisdi01")
                .build();
    }
    
    private static Team buildAthletics() {
        return TeamBuilder.newBuilder(TeamId.of("PHA"))
                .withMainName("Philadelphia")
                .withNickname("Athletics")
                .withAbbreviation("PHI")
                .withBatters(
                        "welchfr01",
                        "perkicy01",
                        "millebi02",
                        "hausejo01",
                        "halesa01",
                        "galloch01",
                        "dykesji01",
                        "matthwi02")
                .withPitchers(
                        "rommeed01",
                        "hastybo01",
                        "harrisl01",
                        "nayloro01")
                .build();
    }
    
    private static Team buildWhiteSox() {
        return TeamBuilder.newBuilder(TeamId.of("CHA"))
                .withMainName("Chicago")
                .withNickname("White Sox")
                .withAbbreviation("CHI")
                .withBatters(
                        "hoopeha01",
                        "mostijo01",
                        "falkbi01",
                        "collied01",
                        "kammwi01",
                        "sheelea01",
                        "schalra01",
                        "mccleha01")
                .withPitchers(
                        "roberch01",
                        "faberre01",
                        "cvengmi01",
                        "blankte01",
                        "leverdi01")
                .build();
    }
    
    
    public static void main(String[] args) {
        playSeriesAndPrintBoxScores(0, 4, 4);
    }

    public static void playSeriesAndPrintBoxScores(int homeTeamIndex, int visitingTeamIndex, int numberOfGames) {
        AL1923 league = new AL1923();
        ImmutableList<Team> teams = league.league.getTeams();
        List<BoxScore> boxScores = league.runSeries(teams.get(homeTeamIndex), teams.get(visitingTeamIndex), numberOfGames);
        boxScores.forEach(AL1923::print);
    }

    public static void playCompleteLeague(int times) {
        for (int n = 1; n <= times; ++n) {
            System.out.println("#" + n + ":");
            AL1923 league = new AL1923();
            Standings standings = league.run();
            print(standings);
            System.out.println();
            BattingLeaders<Integer> hrLeaders = league.league.getBattingLeaders(BattingStat.HOMERUNS, 5);
            printLeaders("HR", hrLeaders);
            System.out.println();
            BattingLeaders<Integer> rbiLeaders = league.league.getBattingLeaders(BattingStat.RUNS_BATTED_IN, 5);
            printLeaders("RBI", rbiLeaders);
            System.out.println();
            BattingLeaders<Average> baLeaders = league.league.getBattingLeaders(BattingStat.BATTING_AVERAGE, 5, 400);
            printLeaders("Batting Average", baLeaders);
        }
    }
    
    private static void printLeaders(String stat, BattingLeaders<?> leaders) {
        System.out.println(stat + " Leaders:");
        PlayerFactory pf = PlayerFactory.defaultFactory();
        Padding namePadding = Padding.of(20);
        Padding valuePadding = Padding.of(5);
        leaders.getEntries().forEach(e -> {
            Player p = pf.getPlayer(e.getPlayerId());
            System.out.println(namePadding.right(p.getName().getShortForm()) + valuePadding.left(e.getValue()));
        });
    }
}
