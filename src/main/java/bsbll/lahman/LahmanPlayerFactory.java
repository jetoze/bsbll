package bsbll.lahman;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.player.Player;
import bsbll.player.PlayerFactory;
import bsbll.player.PlayerId;
import bsbll.player.PlayerName;
import tzeth.collections.ImCollectors;

@Immutable
public final class LahmanPlayerFactory implements PlayerFactory {
    private static final String DEFAULT_PATH = "/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/People.csv";
    
    // TODO: We keep it simple for now and just pre-load the entire file into memory.
    // Eventually we can make this a bit more sophisticated.
    
    private final ImmutableMap<PlayerId, Player> players;
    
    public LahmanPlayerFactory(File file) {
        requireNonNull(file);
        this.players = load(file);
    }
    
    public static LahmanPlayerFactory defaultFactory() {
        return DefaultFactoryHolder.INSTANCE;
    }
    
    private static final class DefaultFactoryHolder {
        private static final LahmanPlayerFactory INSTANCE = new LahmanPlayerFactory(new File(DEFAULT_PATH));
    }
    
    private static ImmutableMap<PlayerId, Player> load(File file) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines.map(s -> parseLine(s))
                    .filter(p -> p != null)
                    .collect(ImCollectors.toMap(Player::getId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Nullable
    private static Player parseLine(String s) {
        String[] parts = s.split(",");
        if (parts.length < 15) {
            return null;
        }
        String id = parts[0].trim();
        String firstName = parts[13].trim();
        String lastName = parts[14].trim();
        return new Player(PlayerId.of(id), PlayerName.fromFirstAndLastName(firstName, lastName));
    }

    @Override
    public Player getPlayer(PlayerId id) {
        requireNonNull(id);
        Player p = players.get(id);
        checkArgument(p != null, "No player exists with ID %s", id);
        return p;
    }
}
