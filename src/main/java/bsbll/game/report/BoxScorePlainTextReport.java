package bsbll.game.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Streams;

import bsbll.NameMode;
import bsbll.game.BattingEvent;
import bsbll.game.BoxScore;
import bsbll.game.Inning;
import bsbll.game.PlayerGameStats;
import bsbll.game.event.DoubleEvent;
import bsbll.game.event.GameEvents;
import bsbll.game.event.HitByPitchEvent;
import bsbll.game.event.HomerunEvent;
import bsbll.game.event.TripleEvent;
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
        
        ImmutableMap<Inning.Half, GameEvents> eventsByHalf = boxScore.getGameEvents().splitByInningHalf();
        
        writeBattingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writeBattingEvents(eventsByHalf.get(Inning.Half.TOP), linesBuilder);
        linesBuilder.add("");
        writeBattingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writeBattingEvents(eventsByHalf.get(Inning.Half.BOTTOM), linesBuilder);
        linesBuilder.add("");
        
        writePitchingStats(boxScore.getVisitingTeam(), boxScore.getVisitingLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writePitchingEvents(eventsByHalf.get(Inning.Half.BOTTOM), linesBuilder);
        linesBuilder.add("");
        writePitchingStats(boxScore.getHomeTeam(), boxScore.getHomeLineup(), 
                boxScore.getPlayerStats(), linesBuilder);
        linesBuilder.add("");
        writePitchingEvents(eventsByHalf.get(Inning.Half.TOP), linesBuilder);
        linesBuilder.add("");
        
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
    
    private void writeBattingEvents(GameEvents events, ImmutableList.Builder<String> lines) {
        writeEvents(events, EventReporter::getBattingEventLines, lines);
    }
    
    private void writeEvents(GameEvents events, Function<EventReporter, List<String>> generator, 
            ImmutableList.Builder<String> lines) {
        if (!events.isEmpty()) {
            EventReporter reporter = new EventReporter(events);
            lines.addAll(generator.apply(reporter));
        }
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
    
    private void writePitchingEvents(GameEvents events, ImmutableList.Builder<String> lines) {
        writeEvents(events, EventReporter::getPitchingEventLines, lines);
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
            lines.addAll(getSpecificBattingEventLines(events.getEvents(DoubleEvent.class), 
                    BattingStat.DOUBLES, this::xbhToString));
            lines.addAll(getSpecificBattingEventLines(events.getEvents(TripleEvent.class), 
                    BattingStat.TRIPLES, this::xbhToString));
            lines.addAll(getSpecificBattingEventLines(events.getEvents(HomerunEvent.class), 
                    BattingStat.HOMERUNS, this::hrToString));
            lines.addAll(getSpecificBattingEventLines(events.getEvents(HitByPitchEvent.class), 
                    BattingStat.HIT_BY_PITCHES, this::hbpToStringForBatter));
            return lines;
        }
        
        private <T extends BattingEvent> List<String> getSpecificBattingEventLines(
                ImmutableList<T> battingEvents, 
                BattingStat<?> type,
                Function<? super T, String> eventToString) {
            if (battingEvents.isEmpty()) {
                return Collections.emptyList();
            }
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder(type.abbrev()).append(": ");
            line = buildBattingEventsString(battingEvents, eventToString, line, lines);
            // Replace the last "; " with a "."
            line.replace(line.length() - 2, line.length(), ".");
            lines.add(line.toString());
            return lines;
        }
        
        private <T extends BattingEvent> StringBuilder buildBattingEventsString(
                Collection<T> battingEvents,
                Function<? super T, String> eventToString,
                StringBuilder line,
                List<String> lines) {
            if (battingEvents.isEmpty()) {
                return line;
            }
            Multimap<Player, T> byPlayer = Multimaps.index(battingEvents, BattingEvent::getBatter);
            for (T evt : byPlayer.values()) {
                String s = eventToString.apply(evt);
                if ((line.length() + s.length()) > MAX_WIDTH) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(s);
            }
            return line;
        }
        
        /**
         * Creates the string representation for an extra-base hit other than a homerun. 
         */
        private String xbhToString(BattingEvent evt) {
            return String.format("%s (%d, off %s); ", 
                    nameOf(evt.getBatter()), evt.getSeasonTotal(), nameOf(evt.getPitcher()));
        }
        
        /**
         * Creates the string representation for a homerun. 
         */
        private String hrToString(HomerunEvent hr) {
            return String.format("%s (%d, %s inning off %s, %d on, %d out); ", 
                    nameOf(hr.getBatter()),
                    hr.getSeasonTotal(),
                    hr.getInning().getNumberAsString(),
                    nameOf(hr.getPitcher()),
                    hr.getRunnersOn(),
                    hr.getOuts());
        }
        
        /**
         * Creates the string representation for a hit-by-pitch, from the batter's perspective. 
         */
        private String hbpToStringForBatter(BattingEvent evt) {
            return String.format("%s (%d, by %s); ", 
                    nameOf(evt.getBatter()), evt.getSeasonTotal(), nameOf(evt.getPitcher()));
        }
        
        public List<String> getPitchingEventLines() {
            List<String> lines = new ArrayList<>();
            // XXX: HBP is reported for both batter and pitcher. We use the getSpecific*Batting*EventLines
            // here as a consequence, which looks and feels a bit odd.
            lines.addAll(getSpecificBattingEventLines(events.getEvents(HitByPitchEvent.class), 
                    BattingStat.HIT_BY_PITCHES, this::hbpToStringForPitcher));
            return lines;
        }

        /**
         * Creates the string representation for a hit-by-pitch, from the pitcher's perspective. 
         */
        private String hbpToStringForPitcher(HitByPitchEvent evt) {
            return String.format("%s (%d, %s); ", 
                    nameOf(evt.getPitcher()), evt.getPitcherSeasonTotal(), nameOf(evt.getBatter()));
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
