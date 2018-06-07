package bsbll.league.report;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import bsbll.NameMode;
import bsbll.league.Standings;
import bsbll.league.Standings.Entry;
import bsbll.report.AbstractPlainTextReport;
import bsbll.team.Record;
import tzeth.strings.Padding;

public final class StandingsPlainTextReport extends AbstractPlainTextReport<Standings> {
    private final NameMode mode;
    private final Padding namePad;
    @Nullable
    private final Comparator<Entry> order;

    public StandingsPlainTextReport(NameMode mode) {
        this.mode = requireNonNull(mode);
        this.namePad = Padding.of(mode.getWidthOfTeamName());
        this.order = null;
    }
    
    public StandingsPlainTextReport(NameMode mode, Comparator<Entry> order) {
        this.mode = requireNonNull(mode);
        this.namePad = Padding.of(mode.getWidthOfTeamName());
        this.order = requireNonNull(order);
    }

    @Override
    public ImmutableList<String> format(Standings standings) {
        ImmutableList<Entry> entries = (order == null)
                ? standings.list()
                : standings.list(order);
                
        Padding wlPadding = Padding.of(5);
        Padding pctPadding = Padding.of(6);
        Padding gbPadding = Padding.of(6);
        Padding rPadding = Padding.of(6);
        Padding raPadding = Padding.of(5);
        
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        String header = namePad.right("") +
                wlPadding.left("W") + wlPadding.left("L") +
                pctPadding.left("PCT") + gbPadding.left("GB") +
                rPadding.left("RS") + raPadding.left("RA");
        builder.add(header);
        for (Entry e : entries) {
            Record record = e.getRecord();
            String line = namePad.right(mode.applyTo(e.getTeam().getName())) +
                    wlPadding.left(record.getWins()) + wlPadding.left(record.getLosses()) +
                    pctPadding.left(record.getWinPct()) + gbPadding.left(e.getGamesBehind()) +
                    rPadding.left(record.getRunsScored()) + raPadding.left(record.getRunsAgainst());
            builder.add(line);
        }
        
        return builder.build();
    }
}
