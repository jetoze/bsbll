package bsbll.player;

import bsbll.lahman.LahmanPlayerFactory;

@FunctionalInterface
public interface PlayerFactory {
    Player getPlayer(PlayerId id);
    default Player getPlayer(String id) {
        return getPlayer(PlayerId.of(id));
    }
    
    public static PlayerFactory defaultFactory() {
        return LahmanPlayerFactory.defaultFactory();
    }
}
