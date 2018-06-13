package bsbll.bases;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@code Advances}.
 */
public final class AdvancesTest {
    @Test
    public void testGrandSlamScores4() {
        Advances advances = grandSlam();
        
        assertEquals(4, advances.getNumberOfRuns());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleScoresNone() {
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        assertEquals(0, advances.getNumberOfRuns());
    }
    
    @Test
    public void testRunnerOnSecondOutAtHomeOnSingleIsAnOut() {
        Advances advances = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.out(Base.SECOND, Base.HOME)
        );
        
        assertEquals(1, advances.getNumberOfOuts());
    }
    
    @Test
    public void testDoublePlayResultsInTwoOuts() {
        Advances advances = Advances.of(
                Advance.out(Base.HOME, Base.FIRST),
                Advance.out(Base.FIRST, Base.SECOND)
        );
        
        assertEquals(2, advances.getNumberOfOuts());
    }

    @Test(expected = InvalidBaseSitutationException.class)
    public void moreThanOneRunnerFromTheSameBaseIsNotAllowed() {
        Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.out(Base.FIRST, Base.THIRD)
        );
    }
    
    @Test(expected = InvalidBaseSitutationException.class)
    public void twoRunnersOnTheSameBaseIsNotAllowed() {
        Advances.of(
                Advance.safe(Base.FIRST, Base.THIRD),
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME)
        );
    }
    
    private static Advances grandSlam() {
        return Advances.of(
                Advance.safe(Base.HOME, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.THIRD, Base.HOME)
        );
    }
}
