package bsbll.bases;

public enum BaseHit {
    // TODO: Should I be in this package?
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    HOMERUN(4);
    
    private final int value;
    
    private BaseHit(int value) {
        this.value = value;
    }
    
    public int value() {
        return value;
    }
}
