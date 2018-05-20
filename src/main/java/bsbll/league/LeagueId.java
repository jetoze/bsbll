package bsbll.league;

public enum LeagueId {

    NA("National Association"),
    
    NL("National League"),
    
    AL("American League");
    
    private LeagueId(String leagueName) {
        this.leagueName = leagueName;
    }
    
    private final String leagueName;
    
    public String getLeagueName() {
        return leagueName;
    }
}
