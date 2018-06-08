package bsbll.game.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Streams;

import bsbll.NameMode;
import bsbll.game.BoxScore;
import bsbll.game.DoubleEvent;
import bsbll.game.ExtraBaseHitEvent;
import bsbll.game.GameEvents;
import bsbll.game.HomerunEvent;
import bsbll.game.Inning;
import bsbll.game.PlayerGameStats;
import bsbll.game.TripleEvent;
import bsbll.player.Player;
import bsbll.report.AbstractPlainTextReport;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStatLine;
import bsbll.stats.Stat;
import bsbll.team.Lineup;
import bsbll.team.Team;
import tzeth.strings.Padding;

public class BoxScorePlainTextReport extends AbstractPlainTextReport<BoxScore> {
    
    // TODO: Support horizontal layout, with the two teams next to each other.
    
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
        writeBattingEvents(boxScore, boxScore.getVisitingTeam(), linesBuilder);
        linesBuilder.add("");
        writeBattingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writeBattingEvents(boxScore, boxScore.getHomeTeam(), linesBuilder);
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
        BattingLineGenerator lineGenerator = BattingLineGenerator.create();
        lines.add(lineGenerator.generateHeader(team.getName().getFullName()));
        BattingStatLine totals = new BattingStatLine();
        for (Player p : lineup.getBattingOrder().getBatters()) {
            BattingStatLine statLine = stats.getBattingLine(p);
            lines.add(lineGenerator.generateStatLine(nameOf(p), statLine));
            totals = totals.plus(statLine);
        }
        lines.add(lineGenerator.generateStatLine("Totals:", totals));
    }
    
    private void writeBattingEvents(BoxScore boxScore, Team team, ImmutableList.Builder<String> lines) {
        Inning.Half half = (team == boxScore.getVisitingTeam())
                ? Inning.Half.TOP
                : Inning.Half.BOTTOM;
        GameEvents relevantEvents = boxScore.getGameEvents().subset(e -> e.getInning().getHalf() == half);
        EventReporter reporter = new EventReporter(relevantEvents);
        lines.addAll(reporter.getBattingEventLines());
    }
    
    private void writePitchingStats(Team team, 
                                    Lineup lineup, 
                                    PlayerGameStats stats, 
                                    ImmutableList.Builder<String> lines) {
        PitchingLineGenerator lineGenerator = PitchingLineGenerator.create();
        lines.add(lineGenerator.generateHeader(team.getName().getFullName()));
        // TODO: This will obviously change once we implement pitcher substitutions.
        Player pitcher = lineup.getPitcher();
        PitchingStatLine statLine = stats.getPitchingLine(pitcher);
        lines.add(lineGenerator.generateStatLine(nameOf(pitcher), statLine));
    }
    
    private static String nameOf(Player p) {
        return p.getName().getShortForm();
    }
    
    
    private static class EventReporter {
        private static final int MAX_WIDTH = 76;
        private final GameEvents events;
        
        public EventReporter(GameEvents events) {
            this.events = events;
        }
        
        public List<String> getBattingEventLines() {
            // TODO: This logic could be separated out to a different class. It's a 
            // perfect example of something that should be covered by unit tests.
            List<String> lines = new ArrayList<>();
            lines.addAll(getExtraBaseHits(events.getEvents(DoubleEvent.class), BattingStat.DOUBLES,
                    this::xbhToString));
            lines.addAll(getExtraBaseHits(events.getEvents(TripleEvent.class), BattingStat.TRIPLES,
                    this::xbhToString));
            lines.addAll(getExtraBaseHits(events.getEvents(HomerunEvent.class), BattingStat.HOMERUNS, 
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
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder(type.abbrev()).append(": ");
            line = addExtraBaseHits(xbhs, eventToString, line, lines);
            // Replace the last "; " with a "."
            line.replace(line.length() - 2, line.length(), ".");
            lines.add(line.toString());
            return lines;
        }
        
        private <T extends ExtraBaseHitEvent> StringBuilder addExtraBaseHits(
                Collection<T> xbhs,
                Function<? super T, String> eventToString,
                StringBuilder line,
                List<String> lines) {
            if (xbhs.isEmpty()) {
                return line;
            }
            Multimap<Player, T> byPlayer = Multimaps.index(xbhs, ExtraBaseHitEvent::getBatter);
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
            return String.format("%s (%d, off %s); ", 
                    nameOf(xbh.getBatter()), xbh.getSeasonTotal(), nameOf(xbh.getPitcher()));
        }
        
        private String hrToString(HomerunEvent hr) {
            // TODO: Add season totals
            return String.format("%s (%d, %s inning off %s, %d on, %d out); ", 
                    nameOf(hr.getBatter()),
                    hr.getSeasonTotal(),
                    hr.getInning().getNumberAsString(),
                    nameOf(hr.getPitcher()),
                    hr.getRunnersOn(),
                    hr.getOuts());
        }
    }
    
    
    // TODO: This class represents a general concept. We have the same issue in e.g. the Standings report.
    // Anywhere we're going to use Paddings to generate a table with a header, we're going to do something
    // like this.
    // Come up with a way to generalize it to not be tied to Stats. But first, generalize it so that
    // we can use it for the pitching part of the box score as well. It's not completely trivial in the
    // absence of a get-method in Stat.
    private static abstract class LineGenerator<T extends Stat<?>> {
        private final ImmutableList<T> stats;
        private final ImmutableList<Padding> paddings;
        
        public LineGenerator(List<T> stats, List<Padding> paddings) {
            this.stats = ImmutableList.copyOf(stats);
            this.paddings = ImmutableList.copyOf(paddings);
            assert paddings.size() == (stats.size() + 1); // Name + Stats
        }
        
        public final String generateHeader(String name) {
            return generate(name, Stat::abbrev);
        }
        
        protected final String generate(String name, Function<? super T, Object> toString) {
            StringBuilder sb = new StringBuilder(paddings.get(0).right(name));
            BiFunction<T, Padding, String> bif = (b, p) -> p.left(toString.apply(b));
            Streams.zip(stats.stream(), paddings.stream().skip(1), bif).forEach(sb::append);
            return sb.toString();
        }
    }
    
    private static class BattingLineGenerator extends LineGenerator<BattingStat<?>> {
        public static BattingLineGenerator create() {
            Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
            Padding pad3 = Padding.of(3);
            Padding pad4 = Padding.of(4);
            return new BattingLineGenerator(
                    Arrays.asList(BattingStat.AT_BATS, BattingStat.RUNS, BattingStat.HITS, BattingStat.RUNS_BATTED_IN), 
                    Arrays.asList(namePadding, pad3, pad3, pad3, pad4));
        }
        
        private BattingLineGenerator(List<BattingStat<?>> stats, List<Padding> paddings) {
            super(stats, paddings);
        }
      
        public String generateStatLine(String name, BattingStatLine line) {
            return generate(name, line::get);
        }
    }
    
    private static class PitchingLineGenerator extends LineGenerator<PitchingStat<?>> {
        public static PitchingLineGenerator create() {
            Padding namePadding = Padding.of(NameMode.FULL.getWidthOfTeamName());
            Padding pad3 = Padding.of(3);
            Padding pad4 = Padding.of(4);
            return new PitchingLineGenerator(
                    Arrays.asList(PitchingStat.INNINGS_PITCHED, PitchingStat.HITS, PitchingStat.RUNS, PitchingStat.EARNED_RUNS, PitchingStat.WALKS, PitchingStat.STRIKEOUTS),
                    Arrays.asList(namePadding, pad4, pad3, pad3, pad3, pad3, pad3)
            );
        }
        
        public PitchingLineGenerator(List<PitchingStat<?>> stats, List<Padding> paddings) {
            super(stats, paddings);
        }
      
        public String generateStatLine(String name, PitchingStatLine line) {
            return generate(name, line::get);
        }
    }
}
