package bsbll.lahman;

import bsbll.player.Player;
import bsbll.player.PlayerFactory;
import bsbll.player.PlayerId;

public class LahmanPlayerFactoryTest {

    public static void main(String[] args) {
        PlayerFactory factory = LahmanPlayerFactory.defaultFactory();
        Player babeRuth = factory.getPlayer(PlayerId.of("ruthba01"));
        System.out.println(babeRuth);
    }
}
