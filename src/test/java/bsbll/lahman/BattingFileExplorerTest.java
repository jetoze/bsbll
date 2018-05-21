package bsbll.lahman;

import bsbll.Year;
import bsbll.card.PlayerCard;
import bsbll.player.PlayerId;

public final class BattingFileExplorerTest {

    public static void main(String[] args) {
        BattingFileExplorer explorer = BattingFileExplorer.defaultExplorer();
        PlayerCard babeRuth1923 = explorer.generatePlayerCard(PlayerId.of("ruthba01"), Year.of(1923));
        System.out.println(babeRuth1923.homeruns());
    }

}
