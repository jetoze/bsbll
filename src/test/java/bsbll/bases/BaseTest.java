package bsbll.bases;

import static org.junit.Assert.*;

import org.junit.Test;

public final class BaseTest {

    @Test
    public void defaultAdvanceOnSingle() {
        verifyDefaultAdvance(BaseHit.SINGLE, Base.FIRST, Base.SECOND, Base.THIRD, Base.HOME);
    }

    @Test
    public void defaultAdvanceOnDouble() {
        verifyDefaultAdvance(BaseHit.DOUBLE, Base.SECOND, Base.THIRD, Base.HOME, Base.HOME);
    }

    @Test
    public void defaultAdvanceOnTriple() {
        verifyDefaultAdvance(BaseHit.TRIPLE, Base.THIRD, Base.HOME, Base.HOME, Base.HOME);
    }

    @Test
    public void defaultAdvanceOnHomerun() {
        verifyDefaultAdvance(BaseHit.HOMERUN, Base.HOME, Base.HOME, Base.HOME, Base.HOME);
    }
    
    private void verifyDefaultAdvance(BaseHit baseHit, Base... expected) {
        assertEquals(4, expected.length);
        Base[] from = new Base[] { Base.HOME, Base.FIRST, Base.SECOND, Base.THIRD };
        for (int n = 0; n < 4; ++n) {
            Advance a = from[n].defaultAdvance(baseHit);
            assertSame(from[n], a.from());
            assertSame(expected[n], a.to());
            assertTrue(a.isSafe());
        }
    }
}
