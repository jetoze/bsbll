package bsbll.card;

import static java.util.Objects.requireNonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import bsbll.Year;
import bsbll.lahman.BattingFileExplorer;
import bsbll.lahman.PitchingFileExplorer;
import bsbll.league.LeagueId;
import bsbll.player.Player;
import bsbll.player.PlayerId;

/**
 * {@code PlayerCardLookup} implementation based on Lahman stats files.
 */
public final class LahmanPlayerCardLookup implements PlayerCardLookup {
    private final Year year;
    private final BattingFileExplorer battingFiles;
    private final PitchingFileExplorer pitchingFiles;
    
    private final PlayerCard leagueCard;
    private final LoadingCache<PlayerId, PlayerCard> battingCards = CacheBuilder.newBuilder()
            .maximumSize(500)
            .build(new CacheLoader<PlayerId, PlayerCard>() {

                @Override
                public PlayerCard load(PlayerId key) throws Exception {
                    return battingFiles.generatePlayerCard(key, year);
                }
            });
    private final LoadingCache<PlayerId, PlayerCard> pitchingCards = CacheBuilder.newBuilder()
            .maximumSize(250)
            .build(new CacheLoader<PlayerId, PlayerCard>() {

                @Override
                public PlayerCard load(PlayerId key) throws Exception {
                    return pitchingFiles.generatePlayerCard(key, year, leagueCard);
                }
            });
    
    public LahmanPlayerCardLookup(LeagueId leagueId, Year year) {
        this(leagueId, year, BattingFileExplorer.defaultExplorer(), PitchingFileExplorer.defaultExplorer());
    }

    public LahmanPlayerCardLookup(LeagueId leagueId, Year year, BattingFileExplorer battingFiles,
            PitchingFileExplorer pitchingFiles) {
        this.year = requireNonNull(year);
        this.battingFiles = requireNonNull(battingFiles);
        this.pitchingFiles = requireNonNull(pitchingFiles);
        this.leagueCard = battingFiles.generateLeagueCard(leagueId, year);
    }

    @Override
    public PlayerCard getBattingCard(Player player) {
        return battingCards.getUnchecked(player.getId());
    }

    @Override
    public PlayerCard getPitchingCard(Player player) {
        return pitchingCards.getUnchecked(player.getId());
    }

    @Override
    public PlayerCard getLeagueCard() {
        return leagueCard;
    }
}
