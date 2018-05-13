package bsbll.research;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test for EventField.
 */
public final class EventFieldTest {
    
    @Test
    public void testBasicOnly() {
        EventField field = EventField.fromString("S7");
        assertEquals("S7", field.getBasicPlay());
        assertNoModifiers(field);
        assertEquals("", field.getAdvanceField());
    }
    
    @Test
    public void testBasicWithModifiers() {
        EventField field = EventField.fromString("54(B)/BG25/SH");
        assertEquals("54(B)", field.getBasicPlay());
        assertEquals(Arrays.asList("BG25", "SH"), field.getModifiers());
        assertEquals("", field.getAdvanceField());
    }

    @Test
    public void testBasicWithAdvance() {
        EventField field = EventField.fromString("CS2(2E4).1-3");
        assertEquals("CS2(2E4)", field.getBasicPlay());
        assertNoModifiers(field);
        assertEquals("1-3", field.getAdvanceField());
    }

    public void assertNoModifiers(EventField field) {
        assertTrue("Expected no modifiers but got " + field.getModifiers(), field.getModifiers().isEmpty());
    }
    
    @Test
    public void testFullForm() {
        EventField field = EventField.fromString("54(B)/BG25/SH.1-2");
        assertEquals("54(B)", field.getBasicPlay());
        assertEquals(Arrays.asList("BG25", "SH"), field.getModifiers());
        assertEquals("1-2", field.getAdvanceField());
    }
    
    @Test
    public void testErrorNotationInAdvanceSection() {
        EventField field = EventField.fromString("D7.1-H;B-3(E5/THH)");
        assertEquals("D7", field.getBasicPlay());
        assertNoModifiers(field);
        assertEquals("1-H;B-3(E5/THH)", field.getAdvanceField());
    }
    
}
