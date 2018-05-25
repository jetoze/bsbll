package bsbll.league.report;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import bsbll.league.Standings;
import bsbll.league.Standings.Entry;
import bsbll.report.AbstractPlainTextReport;
import bsbll.team.Record;
import bsbll.team.TeamName.Mode;
import tzeth.strings.Padding;

public final class StandingsPlainTextReport extends AbstractPlainTextReport<Standings> {
    @Nullable
    private final Comparator<Entry> order;
    
    
    public StandingsPlainTextReport(Mode mode) {
        super(mode);
        this.order = null;
    }
    
    public StandingsPlainTextReport(Mode mode, Comparator<Entry> order) {
        super(mode);
        this.order = requireNonNull(order);
    }

    @Override
    public ImmutableList<String> format(Standings standings) {
        ImmutableList<Entry> entries = (order == null)
                ? standings.list()
                : standings.list(order);
                
        Padding wlPadding = Padding.of(4);
        Padding pctPadding = Padding.of(6);
        Padding gbPadding = Padding.of(6);
        Padding rPadding = Padding.of(7);
        Padding raPadding = Padding.of(5);
        
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        String header = getNamePadding().right("") +
                wlPadding.left("W") + wlPadding.left("L") +
                pctPadding.left("PCT") + gbPadding.left("GB") +
                rPadding.left("R") + raPadding.left("RA");
        builder.add(header);
        for (Entry e : entries) {
            Record record = e.getRecord();
            String line = getTeamName(e.getTeam().getName()) +
                    wlPadding.left(record.getWins()) + wlPadding.left(record.getLosses()) +
                    pctPadding.left(record.getWinPct()) + gbPadding.left(e.getGamesBehind()) +
                    rPadding.left(record.getRunsScored()) + raPadding.left(record.getRunsAgainst());
            builder.add(line);
        }
        
        return builder.build();
    }
}
