package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;


public class BaseEventTest {
    @Test
    public void testCompareTo() throws Exception {

    }


    @Test
    public void testContains() throws Exception {
        ArrayList<TwitterEvent> list = new ArrayList<TwitterEvent>();
        TwitterEvent tweet1 = new TwitterEvent(DateTime.now(), "I love Lucy");
        TwitterEvent tweet2 = new TwitterEvent(DateTime.now(), "This is not a tweet");
        TwitterEvent tweet3 = new TwitterEvent(DateTime.now(), "3 is a magic number");
        list.add(tweet1);
        list.add(tweet2);
        list.add(tweet3);
        assertTrue(list.contains(tweet1));

        DateTime here = new DateTime(2013,1,18,21,15,DateTimeZone.forID("Europe/Berlin"));
        DateTime utc = new DateTime(2013,1,18,20,15,DateTimeZone.UTC);

        // This next line fails (and in this app I want it to succeed)!
        //assertEquals(here, utc);
        // The above means that we need to check the equals implementation of the BaseEvent

        String tweet = "BaseEventTest.testContains checks the DateTime too";
        TwitterEvent hereTweet = new TwitterEvent(here, tweet);
        list.add(hereTweet);

        assertTrue(list.contains(new TwitterEvent(utc, tweet)));

    }


    @Test
    public void testDiff1() throws Exception {
        DateTime now = DateTime.now();
        PumpBasalEvent pumpBasalEvent1 = new PumpBasalEvent(now, "text", 0.80, 2, 1);
        PumpBasalEvent pumpBasalEvent2 = new PumpBasalEvent(now, "text", 0.80, 2, 1);
        assertNull(pumpBasalEvent1.diff(pumpBasalEvent2));
    }


    @Test
    public void testDiff2() throws Exception {
        DateTime now = DateTime.now();
        PumpBasalEvent pumpBasalEvent1 = new PumpBasalEvent(now, "text", 0.80, 1, 1);
        PumpBasalEvent pumpBasalEvent2 = new PumpBasalEvent(now, "text", 0.85, 2, 1);
        assertNotNull(pumpBasalEvent1.diff(pumpBasalEvent2), pumpBasalEvent1.diff(pumpBasalEvent2, true));
    }

    @Test
    public void testDiff3() throws Exception {
        DateTime now = DateTime.now();
        PumpBasalEvent pumpBasalEvent1 = new PumpBasalEvent(now, "text", 0.80, 1, 1);
        PumpBasalEvent pumpBasalEvent2 = new PumpBasalEvent(now.minus(Minutes.minutes(2)), "text", 0.85, 2, 1);
        assertNotNull(pumpBasalEvent1.diff(pumpBasalEvent2), pumpBasalEvent1.diff(pumpBasalEvent2, true));
    }

    @Test
    public void testEquals1() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new PumpEvent(now, "", 0, Unit.none);
        BaseEvent e2 = new PumpEvent(now, "", 0, Unit.none);
        assertTrue(e1.equals(e2), e1.diff(e2));
        assertTrue(e2.equals(e1), e2.diff(e1));
    }
    @Test
    public void testEquals2() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new PumpEvent(now, "A", 0, Unit.none);
        BaseEvent e2 = new PumpEvent(now, "B", 0, Unit.none);
        assertFalse(e1.equals(e2), e1.diff(e2));
        assertFalse(e2.equals(e1), e2.diff(e1));
    }
    @Test
    public void testEquals3() throws Exception {
        DateTime now = DateTime.now();
        BaseEvent e1 = new PumpEvent(now,  "", 0.0, Unit.none);
        BaseEvent e2 = new PumpEvent(now,  "", 0, Unit.none);
        assertFalse(e1.equals(e2), e1.diff(e2));
        assertFalse(e2.equals(e1), e2.diff(e1));
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
        BaseEvent event1 = new CarbEvent(DateTime.now(), 10.0, null, null);
        BaseEvent event2 = new CarbEvent(DateTime.now().minus(Days.days(2)), 10.0, null, null);
        assertFalse(event1.sameDayAs(event2));
        assertFalse(event2.sameDayAs(event1));
    }
    @Test
    public void testSameDayAs2() throws Exception {
        BaseEvent event1 = new CarbEvent(DateTime.now(), 11, null, null);
        BaseEvent event2 = new CarbEvent(DateTime.now().minus(Minutes.minutes(2)), 9, null, null);
        assertTrue(event1.sameDayAs(event2));
        assertTrue(event2.sameDayAs(event2));
    }
    @Test
    public void testSameDayAs3() throws Exception {
        BaseEvent e1 = new CarbEvent(DateTime.now(), 8, null, null);
        BaseEvent event2 = new CarbEvent(e1.startTime, e1.value, null, null);
        assertTrue(e1.sameDayAs(event2));
        assertTrue(event2.sameDayAs(e1));
    }

    @Test
    public void testSameDayAs4() throws Exception {
        BaseEvent e1 = new CarbEvent(DateTime.now(), 10.0, null, null);
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
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 1, 1, 1, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(60*60 + 60 + 1, event.startTime.getSecondOfDay());
    }
    @Test
    public void testGetTimeOfDayPercent1() throws Exception {
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 0, 0, 0, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 0);
    }
    @Test
    public void testGetTimeOfDayPercent2() throws Exception {
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 6, 0, 0, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 25);
    }
    @Test
    public void testGetTimeOfDayPercent3() throws Exception {
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 12, 0, 0, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 50);
    }
    @Test
    public void testGetTimeOfDayPercent4() throws Exception {
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 18, 0, 0, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 75);
    }
    @Test
    public void testGetTimeOfDayPercent5() throws Exception {
        BaseEvent event = new PumpEvent(new DateTime(2012, 12, 28, 23, 59, 59, DateTimeZone.UTC), "", 0, Unit.none);
        assertEquals(event.getTimeOfDayPercent(), 99);
    }

    @Test
    public void testSubTypeOverrides() throws Exception {
        ArrayList<BaseEvent> events = new ArrayList<BaseEvent>();
        events.add(new BloodGlucoseEvent(DateTime.now(), 3.9, null, null));
        events.add(new CarbEvent(DateTime.now(), 30, null, null));
        events.add(new PumpBasalEvent(DateTime.now(), "Teext", 0.9, null, null));
        events.add(new PumpBolusEvent(DateTime.now(), 3.9, null, null));
        events.add(new PumpDailyDoseEvent(DateTime.now(), "Total", 3.9));
        events.add(new PumpEvent(DateTime.now(), "Prime"));
        events.add(new SportEvent(DateTime.now(), Source.Endomondo, "Running", 3.9, Unit.km));
        events.add(new TwitterEvent(DateTime.now(), "A test"));
        ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for (BaseEvent event: events) {
            json.add(event.toJson());
        }
        String string = json.toString();
        assertTrue(string.contains("PumpEvent"), string);
        assertTrue(string.contains("TwitterEvent"), string);
        assertTrue(string.contains("bG_color"), string);

    }
}
