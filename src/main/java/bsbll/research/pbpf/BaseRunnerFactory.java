package bsbll.research.pbpf;

import java.util.HashMap;
import java.util.Map;

import bsbll.game.BaseRunner;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.player.PlayerName;

final class BaseRunnerFactory {
    private static final PlayerName PLAYER_NAME = new PlayerName("John", "Doe");
    
    private final Map<PlayerId, Player> players = new HashMap<>();
    
    public BaseRunner getBaseRunner(ParsedPlay play) {
        if (play.getBatterId().equals(play.getPitcherId())) {
            System.out.println("Darn");
        }
        return new BaseRunner(getPlayer(play.getBatterId()), getPlayer(play.getPitcherId()));
    }
    
    public Player getPlayer(PlayerId id) {
        return players.computeIfAbsent(id, x -> new Player(x, PLAYER_NAME));
    }
}
