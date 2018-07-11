package bsbll.game.params;

import static tzeth.preconds.MorePreconditions.checkInRange;

import p3.Persister;

public abstract class AdvanceDistributionKey {
    private final int outs;
    
    protected AdvanceDistributionKey(int outs) {
        this.outs = checkInRange(outs, 0, 2);
    }

    protected abstract void store(Persister p);
    
    final int getNumberOfOuts() {
        return outs;
    }
}
