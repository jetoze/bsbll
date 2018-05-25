package bsbll.team;

import static java.util.Objects.requireNonNull;

public final class Record {
    private final WLT wlt;
    private final RunDifferential runDiff;

    public Record(WLT wlt, RunDifferential runDiff) {
        this.wlt = requireNonNull(wlt);
        this.runDiff = requireNonNull(runDiff);
    }

    public WLT getWlt() {
        return wlt;
    }

    public RunDifferential getRunDifferential() {
        return runDiff;
    }
    
    public Record add(Record other) {
        return new Record(this.wlt.add(other.wlt), this.runDiff.add(other.runDiff));
    }

}
