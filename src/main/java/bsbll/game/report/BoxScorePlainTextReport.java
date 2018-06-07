package bsbll.game.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Streams;

import bsbll.NameMode;
import bsbll.game.BoxScore;
import bsbll.game.ExtraBaseHitEvent;
import bsbll.game.HomerunEvent;
import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.report.AbstractPlainTextReport;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStatLine;
import bsbll.team.Lineup;
import bsbll.team.Team;
import tzeth.strings.Padding;

public class BoxScorePlainTextReport extends AbstractPlainTextReport<BoxScore> {
    public BoxScorePlainTextReport() {
    }
    
    @Override
    public ImmutableList<String> format(BoxScore boxScore) {
        ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
        
        LineScorePlainTextReport lineScoreReport = new LineScorePlainTextReport(NameMode.ABBREV);
        linesBuilder.addAll(lineScoreReport.format(boxScore.getLineScore()));
        linesBuilder.add("");
        
        writeBattingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writeBattingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        
        EventReporter events = new EventReporter(boxScore);
        linesBuilder.addAll(events.getBattingEventLines());
        linesBuilder.add("");
        
        writePitchingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writePitchingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        
        // TODO: Pitching Events go here.
        
        return linesBuilder.build();
    }
    
    private void writeBattingStats(Team team, 
                                   Lineup lineup, 
                                   PlayerGameStats stats, 
                                   ImmutableList.Builder<String> lines) {
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        BoxScoreLine lineGenerator = new BoxScoreLine(
                Arrays.asList(BattingStat.AT_BATS, BattingStat.RUNS, BattingStat.HITS, BattingStat.RUNS_BATTED_IN), 
                Arrays.asList(namePadding, pad3, pad3, pad3, pad4));
        lines.add(lineGenerator.generateHeader(team.getName().getFullName()));
        BattingStatLine totals = new BattingStatLine();
        for (Player p : lineup.getBattingOrder().getBatters()) {
            BattingStatLine statLine = stats.getBattingLine(p);
            // TODO: Use the player name, once we have it
            lines.add(lineGenerator.generateStatLine(p.getId().toString(), statLine));
            totals = totals.plus(statLine);
        }
        lines.add(lineGenerator.generateStatLine("Totals:", totals));
    }
    
    private void writePitchingStats(Team team, 
                                    Lineup lineup, 
                                    PlayerGameStats stats, 
                                    ImmutableList.Builder<String> lines) {
        // TODO: Winning and losing pitchers, and their new records.
        Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
        Padding pad3 = Padding.of(3);
        Padding pad4 = Padding.of(4);
        lines.add(namePadding.right(team.getName()) +
                pad4.left(PitchingStat.INNINGS_PITCHED.abbrev()) +
                pad3.left(PitchingStat.HITS.abbrev()) +
                pad3.left(PitchingStat.RUNS.abbrev()) +
                pad3.left(PitchingStat.EARNED_RUNS.abbrev()) +
                pad3.left(PitchingStat.WALKS.abbrev()) +
                pad3.left(PitchingStat.STRIKEOUTS.abbrev()));
        // TODO: This will obviously change once we implement pitcher substitutions.
        Player pitcher = lineup.getPitcher();
        PitchingStatLine statLine = stats.getPitchingLine(pitcher);
        // TODO: Use the player name, once we have it
        lines.add(namePadding.right(pitcher) +
                pad4.left(statLine.get(PitchingStat.INNINGS_PITCHED)) +
                pad3.left(statLine.get(PitchingStat.HITS)) +
                pad3.left(statLine.get(PitchingStat.RUNS)) +
                pad3.left(statLine.get(PitchingStat.EARNED_RUNS)) +
                pad3.left(statLine.get(PitchingStat.WALKS)) +
                pad3.left(statLine.get(PitchingStat.STRIKEOUTS)));
    }
    
    
    private static class EventReporter {
        private static final int MAX_WIDTH = 76;
        private final BoxScore boxScore;
        private final Map<Player, Team> teamLookup = new HashMap<>();
        
        public EventReporter(BoxScore boxScore) {
            this.boxScore = boxScore;
        }
        
        public List<String> getBattingEventLines() {
            // TODO: This logic could be separated out to a different class. It's a 
            // perfect example of something that should be covered by unit tests.
            List<String> lines = new ArrayList<>();
            lines.addAll(getExtraBaseHits(boxScore.getGameEvents().getDoubles(), BattingStat.DOUBLES,
                    this::xbhToString));
            lines.addAll(getExtraBaseHits(boxScore.getGameEvents().getTriples(), BattingStat.TRIPLES,
                    this::xbhToString));
            lines.addAll(getExtraBaseHits(boxScore.getGameEvents().getHomeruns(), BattingStat.HOMERUNS, 
                    this::hrToString));
            return lines;
        }
        
        private <T extends ExtraBaseHitEvent> List<String> getExtraBaseHits(
                ImmutableList<T> xbhs, 
                BattingStat<?> type,
                Function<? super T, String> eventToString) {
            if (xbhs.isEmpty()) {
                return Collections.emptyList();
            }
            Multimap<Team, ? extends T> byTeam = Multimaps.index(xbhs, this::getTeamForBatter);
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder(type.abbrev()).append(": ");
            line = addExtraBaseHits(boxScore.getVisitingTeam(), byTeam.get(boxScore.getVisitingTeam()), 
                    eventToString, line, lines);
            line = addExtraBaseHits(boxScore.getHomeTeam(), byTeam.get(boxScore.getHomeTeam()), 
                    eventToString, line, lines);
            // Replace the last "; " with a "."
            line.replace(line.length() - 2, line.length(), ".");
            lines.add(line.toString());
            return lines;
        }
        
        private Team getTeamForBatter(ExtraBaseHitEvent e) {
            // Memoize the results to avoid repeated linear searches through batting order.
            return teamLookup.computeIfAbsent(e.getBatter(), b -> boxScore.getTeam(b));
        }
        
        private <T extends ExtraBaseHitEvent> StringBuilder addExtraBaseHits(
                Team team, 
                Collection<T> xbhs,
                Function<? super T, String> eventToString,
                StringBuilder line,
                List<String> lines) {
            if (xbhs.isEmpty()) {
                return line;
            }
            Multimap<Player, T> byPlayer = Multimaps.index(xbhs, ExtraBaseHitEvent::getBatter);
            line.append(team.getName().getMainName()).append(" ");
            for (T xbh : byPlayer.values()) {
                String e = eventToString.apply(xbh);
                if ((line.length() + e.length()) > MAX_WIDTH) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(e);
            }
            return line;
        }
        
        private String xbhToString(ExtraBaseHitEvent xbh) {
            // TODO: Add season totals. And make it configurable if the pitcher should
            // be included.
            return String.format("%s (off %s); ", xbh.getBatter().getId(), xbh.getPitcher().getId());
        }
        
        private String hrToString(HomerunEvent hr) {
            // TODO: Add season totals
            return String.format("%s (%s inning off %s, %d on, %d out); ", 
                    hr.getBatter().getId(),
                    inningToString(hr.getInning()),
                    hr.getPitcher().getId(),
                    hr.getRunnersOn(),
                    hr.getOuts());
        }
        
        private static String inningToString(int num) {
            // TODO: Move to common utility
            switch (num) {
            case 1:
                return "1st";
            case 2:
                return "2nd";
            case 3:
                return "3rd";
            default:
                return num + "th";
            }
        }
    }
    
    
    private static class BoxScoreLine {
        private final ImmutableList<BattingStat<?>> stats;
        private final ImmutableList<Padding> paddings;
        
        public BoxScoreLine(List<BattingStat<?>> stats, List<Padding> paddings) {
            this.stats = ImmutableList.copyOf(stats);
            this.paddings = ImmutableList.copyOf(paddings);
            assert paddings.size() == (stats.size() + 1); // Name + Stats
        }
        
        public String generateHeader(String name) {
            return generate(name, BattingStat::abbrev);
        }
        
        public String generateStatLine(String name, BattingStatLine line) {
            return generate(name, line::get);
        }
        
        private String generate(String name, Function<? super BattingStat<?>, Object> toString) {
            StringBuilder sb = new StringBuilder(paddings.get(0).right(name));
            BiFunction<BattingStat<?>, Padding, String> bif = (b, p) -> p.left(toString.apply(b));
            Streams.zip(stats.stream(), paddings.stream().skip(1), bif).forEach(sb::append);
            return sb.toString();
        }
    }
    
}
