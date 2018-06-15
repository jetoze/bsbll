package bsbll.bases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    
    @Test
    public void batterAwardedFirstWithEmptyBases() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.NONE);
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirst() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.FIRST);
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnSecond() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.SECOND);
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnThird() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.THIRD);
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirstAndSecond() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.FIRST_AND_SECOND);
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnFirstAndThird() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.FIRST_AND_THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithRunnerOnSecondAndThird() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.SECOND_AND_THIRD);
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.FIRST));
        assertEquals(expected, actual);
    }
    
    @Test
    public void batterAwardedFirstWithBasesLoaded() {
        Advances actual = Advances.batterAwardedFirstBase(OccupiedBases.LOADED);
        
        Advances expected = Advances.of(
                Advance.safe(Base.HOME, Base.FIRST),
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithBasesEmpty() {
        Advances actual = Advances.homerun(OccupiedBases.NONE);
        
        Advances expected = Advances.of(Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirst() {
        Advances actual = Advances.homerun(OccupiedBases.FIRST);
        
        Advances expected = Advances.of(
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnSecond() {
        Advances actual = Advances.homerun(OccupiedBases.SECOND);
        
        Advances expected = Advances.of(
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnThird() {
        Advances actual = Advances.homerun(OccupiedBases.THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirstAndSecond() {
        Advances actual = Advances.homerun(OccupiedBases.FIRST_AND_SECOND);
        
        Advances expected = Advances.of(
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnFirstAndThird() {
        Advances actual = Advances.homerun(OccupiedBases.FIRST_AND_THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void homerunWithRunnerOnSecondAndThird() {
        Advances actual = Advances.homerun(OccupiedBases.SECOND_AND_THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void homerunWithBasesLoaded() {
        Advances actual = Advances.homerun(OccupiedBases.LOADED);
        
        Advances expected = Advances.of(
                Advance.safe(Base.THIRD, Base.HOME),
                Advance.safe(Base.SECOND, Base.HOME),
                Advance.safe(Base.FIRST, Base.HOME),
                Advance.safe(Base.HOME, Base.HOME));
        assertEquals(expected, actual);
    }

    @Test
    public void balkWithNoBodyOnBase() { // ...so to speak
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.NONE);
        
        assertTrue(actual.isEmpty());
    }
    
    @Test
    public void balkWithRunnerOnFirst() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.FIRST);
        
        Advances expected = Advances.of(Advance.safe(Base.FIRST, Base.SECOND));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithRunnerOnSecond() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.SECOND);
        
        Advances expected = Advances.of(Advance.safe(Base.SECOND, Base.THIRD));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithRunnerOnThird() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.THIRD);
        
        Advances expected = Advances.of(Advance.safe(Base.THIRD, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithRunnerOnFirstAndSecond() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.FIRST_AND_SECOND);
        
        Advances expected = Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithRunnerOnFirstAndThird() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.FIRST_AND_THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.THIRD, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithRunnerOnSecondAndThird() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.SECOND_AND_THIRD);
        
        Advances expected = Advances.of(
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
        assertEquals(expected, actual);
    }
    
    @Test
    public void balkWithBasesLoaded() {
        Advances actual = Advances.runnersAdvanceOneBase(OccupiedBases.LOADED);
        
        Advances expected = Advances.of(
                Advance.safe(Base.FIRST, Base.SECOND),
                Advance.safe(Base.SECOND, Base.THIRD),
                Advance.safe(Base.THIRD, Base.HOME));
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
