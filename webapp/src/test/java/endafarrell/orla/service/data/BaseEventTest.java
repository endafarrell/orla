package endafarrell.orla.service.data;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class BaseEventTest {
    @Test
    public void testCompareTo() throws Exception {

    }

    @Test
    public void testEquals1() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new BaseEvent(now, Source.Orla, "", 0, Unit.none);
        BaseEvent e2 = new BaseEvent(now, Source.Orla, "", 0, Unit.none);
        assertTrue(e1.equals(e2));
        assertTrue(e2.equals(e1));
    }
    @Test
    public void testEquals2() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new BaseEvent(now, Source.Orla, "A", 0, Unit.none);
        BaseEvent e2 = new BaseEvent(now, Source.Orla, "B", 0, Unit.none);
        assertFalse(e1.equals(e2));
        assertFalse(e2.equals(e1));
    }
    @Test
    public void testEquals3() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new BaseEvent(now, Source.Orla, "", 0.0, Unit.none);
        BaseEvent e2 = new BaseEvent(now, Source.Orla, "", 0, Unit.none);
        assertFalse(e1.equals(e2));
        assertFalse(e2.equals(e1));
    }
    @Test
    public void testHashCode() throws Exception {

    }

    @Test
    public void testGetStartTime() throws Exception {

    }

    @Test
    public void testGetEndTime() throws Exception {

    }

    @Test
    public void testSetEndTime() throws Exception {

    }

    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testSameDayAs1() throws Exception {
        BaseEvent event1 = new BaseEvent(DateTime.now(), Source.Orla, "A", 10.0, Unit.none);
        BaseEvent event2 = new BaseEvent(DateTime.now().minus(Days.days(2)), Source.Orla, "A", 10.0, Unit.none);
        assertFalse(event1.sameDayAs(event2));
        assertFalse(event2.sameDayAs(event1));
    }
    @Test
    public void testSameDayAs2() throws Exception {
        BaseEvent event1 = new BaseEvent(DateTime.now(), Source.Orla, "A", 10.0, Unit.none);
        BaseEvent event2 = new BaseEvent(DateTime.now().minus(Minutes.minutes(2)), Source.Orla, "A", 10.0, Unit.none);
        assertTrue(event1.sameDayAs(event2));
        assertTrue(event2.sameDayAs(event2));
    }
    @Test
    public void testSameDayAs3() throws Exception {
        BaseEvent e1 = new BaseEvent(DateTime.now(), Source.Orla, "A", 10.0, Unit.none);
        BaseEvent event2 = new BaseEvent(e1.startTime, e1.source, e1.text, e1.value, e1.unit);
        assertTrue(e1.sameDayAs(event2));
        assertTrue(event2.sameDayAs(e1));
    }

    @Test
    public void testSameDayAs4() throws Exception {
        BaseEvent e1 = new BaseEvent(DateTime.now(), Source.Orla, "", 10.0, Unit.none);
        assertFalse(e1.sameDayAs(null));
    }

    @Test
    public void testGetUnit() throws Exception {

    }

    @Test
    public void testGetSource() throws Exception {

    }

    @Test
    public void testGetText() throws Exception {

    }
    @Test
    public void testGetTimeOfDayPercent0() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 1, 1, 1, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(60*60 + 60 + 1, event.startTime.getSecondOfDay());
    }
    @Test
    public void testGetTimeOfDayPercent1() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 0, 0, 0, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 0);
    }
    @Test
    public void testGetTimeOfDayPercent2() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 6, 0, 0, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 25);
    }
    @Test
    public void testGetTimeOfDayPercent3() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 12, 0, 0, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 50);
    }
    @Test
    public void testGetTimeOfDayPercent4() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 18, 0, 0, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 75);
    }
    @Test
    public void testGetTimeOfDayPercent5() throws Exception {
        BaseEvent event = new BaseEvent(new DateTime(2012, 12, 28, 23, 59, 59, DateTimeZone.UTC), Source.Orla, "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 99);
    }
}
