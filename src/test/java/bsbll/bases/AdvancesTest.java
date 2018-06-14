package bsbll.bases;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

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
    
    @Test
    public void batterAwardedFirstWithEmptyBases() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of());
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirst() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.FIRST));
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnSecond() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.SECOND));
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnThird() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.THIRD));
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirstAndSecond() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.FIRST, Base.SECOND));
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirstAndThird() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.FIRST, Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnSecondAndThird() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.SECOND, Base.THIRD));
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithBasesLoaded() {
        Advances actual = Advances.batterAwardedFirstBase(ImmutableSet.of(Base.FIRST, Base.SECOND, Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithBasesEmpty() {
        Advances actual = Advances.homerun(ImmutableSet.of());
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirst() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.FIRST));
        
        Advances expected = Advances.of(
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnSecond() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.SECOND));
        
        Advances expected = Advances.of(
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnThird() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirstAndSecond() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.FIRST, Base.SECOND));
        
        Advances expected = Advances.of(
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirstAndThird() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.FIRST, Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnSecondAndThird() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.SECOND, Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void homerunWithBasesLoaded() {
        Advances actual = Advances.homerun(ImmutableSet.of(Base.FIRST, Base.SECOND, Base.THIRD));
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
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
