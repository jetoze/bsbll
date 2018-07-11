package bsbll.game;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advances;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.RunsScored.Run;
import bsbll.game.params.GamePlayParams;
import bsbll.game.params.OutAdvanceKey;
import bsbll.game.params.OutLocation;
import bsbll.game.play.EventType;
import bsbll.game.play.Play;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;

/**
 * The official scorer's reconstruction of an inning that contains potentially unearned runs.
 */
final class ReconstructedInning {
    // TODO: The current implementation uses getMostCommonXXX to get the reconstructed 
    // base advances. Should we be using pickOne instead?
    
    private final Inning inning;
    private final ImmutableList<Play> actualPlays;
    // XXX: GamePlayParams offers more functionality than what is needed here. Refactor to 
    // pass in a trimmed down service.
    private final GamePlayParams gamePlayParams;
    private BaseSituation reconstructedBaseSituation = BaseSituation.empty();
    private BaseSituation actualBaseSituation = BaseSituation.empty();
    private int outs;
    private boolean batterShouldBeOut;
    
    // For error reporting
    private final List<Play> reconstructedPlays = new ArrayList<>();
    private Play currentPlay;
    
    public ReconstructedInning(Inning inning, List<Play> actualPlays, GamePlayParams gamePlayParams) {
        this.inning = requireNonNull(inning);
        this.actualPlays = ImmutableList.copyOf(actualPlays);
        this.gamePlayParams = requireNonNull(gamePlayParams);
    }

    public static ReconstructedInning of(HalfInning.Summary summary, GamePlayParams gamePlayParams) {
        return new ReconstructedInning(summary.getInning(), summary.getPlays(), gamePlayParams);
    }
    
    public ImmutableList<Run> getEarnedRuns() {
        // TODO: This situation needs to change once we implement pitcher substitutions, when the
        // pitcher responsible for a batter can change due to a fielder's choice.
        // TODO: Also, when we have pitcher substitutions we must distinguish between team unearned 
        // runs and pitcher unearned runs. Add class EarnedRun, with a flag that tells us if the run
        // is earned for both the team and the pitcher, or just the pitcher.
        ImmutableList.Builder<Run> earnedRuns = ImmutableList.builder();
        Player previousBatter = null;
        
        for (Play actualPlay : actualPlays) {
            currentPlay = actualPlay;
            if (actualPlay.getBatter() != previousBatter) {
                previousBatter = actualPlay.getBatter();
                batterShouldBeOut = false;
            } else if (batterShouldBeOut) {
                continue;
            }
            Play idealPlay = processPlay(actualPlay, earnedRuns);
            actualBaseSituation = actualPlay.advanceRunners(actualBaseSituation).getNewSituation();
            previousBatter = idealPlay.getBatter();
        }
        return earnedRuns.build();
    }

    private Play processPlay(Play actualPlay, ImmutableList.Builder<Run> earnedRuns) {
        try {
            Play idealPlay = getIdealPlay(actualPlay);
            reconstructedPlays.add(idealPlay);
            handleIdealPlay(idealPlay, earnedRuns);
            return idealPlay;
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }

    private void handleIdealPlay(Play idealPlay, ImmutableList.Builder<Run> earnedRuns) {
        if (idealPlay.isNoPlay()) {
            return;
        }
        ResultOfAdvance roa = idealPlay.advanceRunners(reconstructedBaseSituation);
        if (outs < 3) {
            // TODO: Once we implement pitcher substitutions this will have to be treated 
            // differently. The run will be unearned for the team, but could still be earned
            // for the pitcher.
            roa.getRunnersThatScored().stream()
                .map(br -> new Run(inning, br))
                .forEach(earnedRuns::add);
        }
        reconstructedBaseSituation = roa.getNewSituation();
        outs += idealPlay.getNumberOfOuts();
    }
    
    private Play getIdealPlay(Play actualPlay) {
        if (actualPlay.isErrorOrPassedBall()) {
            return createIdealVersionOfNonIdealPlay(actualPlay);
        } else {
            return tweakAdvancesOfIdealPlayIfNecessary(actualPlay);
        }
    }

    /**
     * Reconstructs an original play that had errors or a passed balls.
     */
    private Play createIdealVersionOfNonIdealPlay(Play actualPlay) {
        assert actualPlay.isErrorOrPassedBall();
        EventType type = actualPlay.getOutcome().getType();
        if (type == EventType.PASSED_BALL) {
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        assert actualPlay.getNumberOfErrors() > 0;
        if (type == EventType.ERROR_ON_FOUL_FLY) {
            batterShouldBeOut = true;
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        if (type == EventType.REACHED_ON_ERROR || type == EventType.OUT) {
            return idealPlayOnOut(actualPlay, type);
        } else if (type.isHit()) {
            return idealPlayOnBaseHit(actualPlay, type);
        } else {
            throw new RuntimeException("TODO: Implement me");
        }
    }

    private Play idealPlayOnOut(Play actualPlay, EventType type) {
        batterShouldBeOut = (type == EventType.REACHED_ON_ERROR); // If OUT, the batter *is* out.
        // TODO: The OutLocation should be the same that was used when the original OUT
        // was turned into a REACHED_ON_ERROR by the GamePlayParams. How do we accomplish
        // that? Store the OutLocation as an optional parameter in PlayOutcome?
        OutLocation location = gamePlayParams.getOutLocation();
        // XXX: outs keeps track of how many outs *should* have been recorded. This can
        // be >= 3. The advance distributions rightfully expect the number of outs to be
        // < 3, so if we end up in that situation we use 2.
        int outsToUse = Math.min(2, outs);
        OutAdvanceKey key = OutAdvanceKey.of(EventType.OUT, location, outsToUse);
        Predicate<? super Advances> predicate = a -> a.getNumberOfOuts() < 2;
        Advances advances = gamePlayParams.getMostCommonAdvancesOnOut(key, reconstructedBaseSituation, predicate);
        return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), new PlayOutcome(EventType.OUT, advances));
    }

    private Play idealPlayOnBaseHit(Play actualPlay, EventType type) {
        BaseHit hit = BaseHit.fromEventType(type);
        // TODO: We should probably not be using the most common advances here, but rather pick one.
        // (cf. the case of REACHED_ON_ERROR)
        Advances advances = gamePlayParams.getMostCommonAdvancesOnBaseHit(hit, reconstructedBaseSituation, Math.min(2, outs));
        return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), new PlayOutcome(type, advances));
    }
    
    /**
     * Returns a new version of an actual play that did not have any errors or passed balls,
     * with the advances updated, if necessary, to match the reconstructed base situation.
     * <p>
     * Take the following example:
     * <ol>
     * <li>Single;</li>
     * <li>Passed Ball - runner advances to second;</li>
     * <li>Single - runner on second scores;</li>
     * </ol>
     * The reconstructed version of the above becomes:
     * <ol>
     * <li>Single;</li>
     * <li>No play - runner stays on first;</li>
     * <li>Single</li>
     * </ol>
     * The third play is already ideal, but we cannot apply its original advances ([2-H], [H-1]) to the
     * reconstructed base situation, because the runner is still on first, not on second.
     * <p>
     * The best way of handling this situation is still up for discussion. For now, we simply pick
     * the most common advance, given the type of play and situation. 
     */
    private Play tweakAdvancesOfIdealPlayIfNecessary(Play play) {
        assert !play.isErrorOrPassedBall();
        if (reconstructedBaseSituation.equals(actualBaseSituation)) {
            return play;
        }
        PlayOutcome actualOutcome = play.getOutcome();
        Advances advances = advancesThatMatchReconstructedSituation(actualOutcome);
        PlayOutcome reconstructedOutcome = new PlayOutcome(actualOutcome.getType(), advances);
        return new Play(play.getBatter(), play.getPitcher(), reconstructedOutcome);
    }
    
    private Advances advancesThatMatchReconstructedSituation(PlayOutcome o) {
        int outsToUse = Math.min(2, outs);
        switch (o.getType()) {
        case HOMERUN:
            return Advances.homerun(reconstructedBaseSituation.getOccupiedBases());
        case SINGLE: /*fall-through*/
        case DOUBLE: /*fall-through*/
        case TRIPLE:
            return gamePlayParams.getMostCommonAdvancesOnBaseHit(
                    BaseHit.fromEventType(o.getType()), 
                    reconstructedBaseSituation, 
                    outsToUse);
        case OUT: /*fall-through*/
        case FIELDERS_CHOICE:
            // Never assume a double (or triple) play.
            Predicate<? super Advances> predicate = a -> a.getNumberOfOuts() < 2;
            return gamePlayParams.getMostCommonAdvancesOnOut(
                    OutAdvanceKey.of(
                            EventType.OUT,
                            // TODO: Same situation as in idealPlayOnOut() - we should use the same OutLocation as the original play
                            gamePlayParams.getOutLocation(), 
                            outsToUse), 
                    reconstructedBaseSituation, 
                    predicate);
        case WALK: /*fall-through*/
        case HIT_BY_PITCH:
            return Advances.batterAwardedFirstBase(reconstructedBaseSituation.getOccupiedBases());
        case STRIKEOUT:
            // TODO: Once we've implemented the possibility of advances on strikeouts, this 
            // must change accordingly (?)
            return Advances.empty();
        case WILD_PITCH:
            // TODO: Use a proper distribution here?
            return Advances.runnersAdvanceOneBase(reconstructedBaseSituation.getOccupiedBases());
        case BALK:
            return Advances.runnersAdvanceOneBase(reconstructedBaseSituation.getOccupiedBases());
        default:
            throw new RuntimeException("TODO: Implement me for " + o.getType());
        }
    }
    
    private void reportError(RuntimeException e) {
        System.out.println(e.getMessage());
        System.out.println();
        System.out.println("Actual plays:");
        actualPlays.forEach(System.out::println);
        System.out.println("Actual base situation:");
        System.out.println(actualBaseSituation);
        System.out.println("Current play:");
        System.out.println(currentPlay);
        System.out.println();
        System.out.println("Reconstructed plays:");
        reconstructedPlays.forEach(System.out::println);
        System.out.println("Reconstructed base situation:");
        System.out.println(reconstructedBaseSituation);
        System.out.println();
        System.out.println("Stacktrace:");
        e.printStackTrace(System.out);
    }
}
