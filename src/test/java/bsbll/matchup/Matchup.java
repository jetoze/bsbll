package bsbll.matchup;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import bsbll.card.DieFactory;
import bsbll.card.PlayerCard;
import bsbll.card.Probability;

/**
 * The matchup between a batter and a pitcher.
 */
public final class Matchup {
    private final PlayerCard batter;
    private final PlayerCard pitcher;
    private final PlayerCard league;

    public Matchup(PlayerCard batter, PlayerCard pitcher, PlayerCard league) {
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
        this.league = requireNonNull(league);
    }
    
    public Outcome run(DieFactory dieFactory) {
        requireNonNull(dieFactory);
        boolean contact = evaluate(PlayerCard::contact, dieFactory);
        return contact
                ? resolveContact(dieFactory)
                : resolveNonContact(dieFactory);
    }
    
    private Outcome resolveContact(DieFactory dieFactory) {
        // This is either a hit or a batted out.
        boolean hit = normalizeAndEvaluate(PlayerCard::hits, PlayerCard::battedOuts, dieFactory);
        return hit
                ? resolveHit(dieFactory)
                : Outcome.OUT;
    }

    private Outcome resolveHit(DieFactory dieFactory) {
        // Either a single or an extrabase hit.
        boolean single = normalizeAndEvaluate(PlayerCard::singles, PlayerCard::extraBaseHits, dieFactory);
        if (single) {
            return Outcome.SINGLE;
        } else {
            return resolveExtraBaseHit(dieFactory);
        }
    }
    
    private Outcome resolveExtraBaseHit(DieFactory dieFactory) {
        // Homerun or not?
        boolean hr = normalizeAndEvaluate(
                PlayerCard::homeruns, 
                c -> c.extraBaseHits().subtract(c.homeruns()), 
                dieFactory);
        return hr
                ? Outcome.HOMERUN
                : resolveDoubleOrTriple(dieFactory);
    }
    
    private Outcome resolveDoubleOrTriple(DieFactory dieFactory) {
        boolean isDouble = normalizeAndEvaluate(PlayerCard::doubles, PlayerCard::triples, dieFactory);
        return isDouble
                ? Outcome.DOUBLE
                : Outcome.TRIPLE;
    }
    
    private Outcome resolveNonContact(DieFactory dieFactory) {
        // Strikeout, or walk/hbp?
        boolean so = normalizeAndEvaluate(
                PlayerCard::strikeouts, 
                c -> c.walks().add(c.hitByPitches()), 
                dieFactory);
        return so
                ? Outcome.STRIKEOUT
                : resolveWalkOrHitByPitch(dieFactory);
    }

    private Outcome resolveWalkOrHitByPitch(DieFactory dieFactory) {
        boolean bb = normalizeAndEvaluate(PlayerCard::walks, PlayerCard::hitByPitches, dieFactory);
        return bb
                ? Outcome.WALK
                : Outcome.HIT_BY_PITCH;
    }
    
    private boolean evaluate(Function<PlayerCard, Probability> category, DieFactory dieFactory) {
        Probability p_batter = category.apply(this.batter);
        Probability p_pitcher = category.apply(this.pitcher);
        Probability p_league = category.apply(this.league);
        Probability log5 = Probability.log5(p_batter, p_pitcher, p_league);
        return log5.test(dieFactory);
    }

    private boolean normalizeAndEvaluate(Function<PlayerCard, Probability> a, 
                                         Function<PlayerCard, Probability> b,
                                         DieFactory dieFactory) {
        ImmutableList<Probability> batterPs = Probability.normalize(a.apply(batter), b.apply(batter));
        ImmutableList<Probability> pitcherPs = Probability.normalize(a.apply(pitcher), b.apply(pitcher));
        ImmutableList<Probability> leaguePs = Probability.normalize(a.apply(league), b.apply(league));
        Probability p_a = Probability.log5(batterPs.get(0), pitcherPs.get(0), leaguePs.get(0));
        return p_a.test(dieFactory);
    }
    

    public static enum Outcome {
        SINGLE,
        DOUBLE,
        TRIPLE,
        HOMERUN,
        STRIKEOUT,
        WALK,
        HIT_BY_PITCH,
        OUT;
        
        public boolean isHit() {
            switch (this) {
            case SINGLE:
            case DOUBLE:
            case TRIPLE:
            case HOMERUN:
                return true;
            default:
                return false;
            }
        }
    }
}
