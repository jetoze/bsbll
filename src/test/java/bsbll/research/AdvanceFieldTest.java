package bsbll.research;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import bsbll.research.AdvanceField.Annotation;

public final class AdvanceFieldTest {

    @Test
    public void noAnnotations() {
        assertTrue(AdvanceField.extractAnnotations("1-2").isEmpty());
    }
    
    @Test
    public void errorAnnotation() {
        verifyAnnotations("1X2(E4)", Annotation.ERROR);
    }

    @Test
    public void errorAnnotationWithIndicator() {
        verifyAnnotations("1-3(E5/TH)", Annotation.ERROR);
    }
    
    @Test
    public void fieldersAnnotation() {
        verifyAnnotations("BX2(8434)", Annotation.FIELDERS);
    }
    
    @Test
    public void annotationsForErrorLeadingToUnearnedRun() {
        verifyAnnotations("2-H(E4/TH)(UR)(NR)", Annotation.ERROR, Annotation.UNEARNED_RUN, Annotation.NO_RBI);
    }
    
    @Test
    public void errorAndFieldingAnnotation() {
        verifyAnnotations("BX3(E4/TH1)(35)", Annotation.ERROR, Annotation.FIELDERS);
    }
    
    private static void verifyAnnotations(String s, Annotation... expected) {
        ImmutableList<Annotation> actual = AdvanceField.extractAnnotations(s);
        assertEquals(ImmutableList.copyOf(expected), actual);
    }
    
}
