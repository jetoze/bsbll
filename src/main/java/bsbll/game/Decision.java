package bsbll.game;

public enum Decision {
    WIN("W"),
    LOSS("L");
    
    private final String abbrev;
    
    private Decision(String abbrev) {
        this.abbrev = abbrev;
    }
    
    public String abbrev() {
        return abbrev;
    }
}
