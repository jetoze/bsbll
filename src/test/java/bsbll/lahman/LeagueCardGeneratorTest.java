package bsbll.lahman;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;

public class LeagueCardGeneratorTest {

    public static void main(String[] args) throws Exception {
        PlayerCard al1923 = LeagueCardGenerator.defaultGenerator().generateCard(LeagueId.AL, Year.of(1923));
        System.out.println(al1923.homeruns());
    }

}
