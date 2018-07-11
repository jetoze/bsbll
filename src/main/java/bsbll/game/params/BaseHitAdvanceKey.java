package bsbll.game.params;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import bsbll.bases.BaseHit;
import p3.Persister;

public final class BaseHitAdvanceKey extends AdvanceDistributionKey {
    private final BaseHit baseHit;
    
    public BaseHitAdvanceKey(BaseHit baseHit, int outs) {
        super(outs);
        this.baseHit = requireNonNull(baseHit);
    }

    BaseHit getType() {
        return baseHit;
    }
    
    @Override
    protected void store(Persister p) {
        Storage.store(this, p);
    }
    
    static BaseHitAdvanceKey restore(Persister p) {
        return Storage.restore(p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseHit, getNumberOfOuts());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BaseHitAdvanceKey) {
            BaseHitAdvanceKey that = (BaseHitAdvanceKey) obj;
            return this.baseHit == that.baseHit && this.getNumberOfOuts() == that.getNumberOfOuts();
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", baseHit, getNumberOfOuts(), 
                (getNumberOfOuts() == 1 ? "out" : "outs"));
    }
    
    
    private static class Storage {
        private static final String TYPE = "Type";
        private static final String OUTS = "Outs";
        
        public static void store(BaseHitAdvanceKey key, Persister p) {
            p.putString(TYPE, key.baseHit.name())
                .putInt(OUTS, key.getNumberOfOuts());
        }
        
        public static BaseHitAdvanceKey restore(Persister p) {
            return new BaseHitAdvanceKey(
                    BaseHit.valueOf(p.getString(TYPE)), 
                    p.getInt(OUTS));
        }
    }
}
