package bsbll.research.pbpf;

import java.io.File;

import com.google.common.collect.ImmutableList;

import bsbll.Year;
import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public final class ErrorCounter extends GameHandler {
    private int gameCount;
    private int inningCount;
    private int errorCount;
    private int playCount;

    @Override
    public void onStartGame(String id) {
        ++gameCount;
    }

    @Override
    public void onEndOfInning(Inning inning, ImmutableList<EventField> fields,
            ImmutableList<PlayOutcome> plays) {
        ++inningCount;
        playCount += plays.size();
        errorCount += plays.stream()
                .mapToInt(PlayOutcome::getNumberOfErrors)
                .sum();
    }

    public static void main(String[] args) {
        Year year = Year.of(1931);
        File folder = PlayByPlayFileUtils.getFolder(year);
        ErrorCounter ec = new ErrorCounter();
        ec.parseAll(folder);
        System.out.println(ec.gameCount);
        System.out.println(ec.inningCount);
        System.out.println(ec.playCount);
        System.out.println(ec.errorCount);
    }
}
