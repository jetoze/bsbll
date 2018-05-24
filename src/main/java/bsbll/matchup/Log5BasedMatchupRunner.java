package bsbll.matchup;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import bsbll.card.DieFactory;
import bsbll.card.PlayerCard;
import bsbll.card.PlayerCardLookup;
import bsbll.card.Probability;
import bsbll.player.Player;

/**
 * {@link MatchupRunner} implementation based on the log5 method.
 * <p>
 * See https://sabr.org/research/matchup-probabilities-major-league-baseball#footnote2_8f5byka.
 */
public final class Log5BasedMatchupRunner implements MatchupRunner {
    private final PlayerCard leagueCard;
    private final PlayerCardLookup playerCardLookup;
    private final DieFactory dieFactory;

    public Log5BasedMatchupRunner(PlayerCardLookup playerCardLookup,
                                  DieFactory dieFactory) {
        this.leagueCard = playerCardLookup.getLeagueCard();
        this.playerCardLookup = playerCardLookup;
        this.dieFactory = requireNonNull(dieFactory);
    }
    
    @Override
    public Outcome run(Player batter, Player pitcher) {
        PlayerCard batterCard = playerCardLookup.getBattingCard(batter);
        PlayerCard pitcherCard = playerCardLookup.getPitchingCard(pitcher);
        boolean contact = evaluate(batterCard, pitcherCard, PlayerCard::contact);
        return contact
                ? resolveContact(batterCard, pitcherCard)
                : resolveNonContact(batterCard, pitcherCard);
    }
    
    private Outcome resolveContact(PlayerCard batter, PlayerCard pitcher) {
        // This is either a hit or a batted out.
        boolean hit = normalizeAndEvaluate(batter, pitcher, PlayerCard::hits, PlayerCard::battedOuts);
        return hit
                ? resolveHit(batter, pitcher)
                : Outcome.OUT;
    }

    private Outcome resolveHit(PlayerCard batter, PlayerCard pitcher) {
        // Either a single or an extrabase hit.
        boolean single = normalizeAndEvaluate(batter, pitcher, PlayerCard::singles, PlayerCard::extraBaseHits);
        if (single) {
            return Outcome.SINGLE;
        } else {
            return resolveExtraBaseHit(batter, pitcher);
        }
    }
    
    private Outcome resolveExtraBaseHit(PlayerCard batter, PlayerCard pitcher) {
        // Homerun or not?
        boolean hr = normalizeAndEvaluate(
                batter,
                pitcher,
                PlayerCard::homeruns, 
                c -> c.extraBaseHits().subtract(c.homeruns()));
        return hr
                ? Outcome.HOMERUN
                : resolveDoubleOrTriple(batter, pitcher);
    }
    
    private Outcome resolveDoubleOrTriple(PlayerCard batter, PlayerCard pitcher) {
        boolean isDouble = normalizeAndEvaluate(batter, pitcher, PlayerCard::doubles, PlayerCard::triples);
        return isDouble
                ? Outcome.DOUBLE
                : Outcome.TRIPLE;
    }
    
    private Outcome resolveNonContact(PlayerCard batter, PlayerCard pitcher) {
        // Strikeout, or walk/hbp?
        boolean so = normalizeAndEvaluate(
                batter,
                pitcher,
                PlayerCard::strikeouts, 
                c -> c.walks().add(c.hitByPitches()));
        return so
                ? Outcome.STRIKEOUT
                : resolveWalkOrHitByPitch(batter, pitcher);
    }

    private Outcome resolveWalkOrHitByPitch(PlayerCard batter, PlayerCard pitcher) {
        boolean bb = normalizeAndEvaluate(batter, pitcher, PlayerCard::walks, PlayerCard::hitByPitches);
        return bb
                ? Outcome.WALK
                : Outcome.HIT_BY_PITCH;
    }
    
    private boolean evaluate(PlayerCard batter, 
                             PlayerCard pitcher, 
                             Function<PlayerCard, Probability> category) {
        Probability p_batter = category.apply(batter);
        Probability p_pitcher = category.apply(pitcher);
        Probability p_league = category.apply(this.leagueCard);
        Probability log5 = Probability.log5(p_batter, p_pitcher, p_league);
        return log5.test(this.dieFactory);
    }

    private boolean normalizeAndEvaluate(PlayerCard batter, 
                                         PlayerCard pitcher,
                                         Function<PlayerCard, Probability> a, 
                                         Function<PlayerCard, Probability> b) {
        ImmutableList<Probability> batterPs = Probability.normalize(a.apply(batter), b.apply(batter));
        ImmutableList<Probability> pitcherPs = Probability.normalize(a.apply(pitcher), b.apply(pitcher));
        ImmutableList<Probability> leaguePs = Probability.normalize(a.apply(this.leagueCard), b.apply(this.leagueCard));
        Probability p_a = Probability.log5(batterPs.get(0), pitcherPs.get(0), leaguePs.get(0));
        return p_a.test(this.dieFactory);
    }

}
