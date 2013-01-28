package endafarrell.orla.service;

import endafarrell.orla.service.data.CarbEvent;
import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.data.TwitterEvent;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class FilterTest {
    @Test
    public void testLast1() throws Exception {

    }

    @Test
    public void testLast2() throws Exception {

    }

    @Test
    public void testPercentiles() throws Exception {

    }

    @Test
    public void testOnly1() throws Exception {

    }

    @Test
    public void testOnly2() throws Exception {
        Collection<Event> events = new ArrayList<Event>(3);
        events.add(new CarbEvent(DateTime.now(), 10.0, null, null));
        events.add(new TwitterEvent(DateTime.now(), "This is a test of the emergency broadcast system"));
        events.add(new CarbEvent(DateTime.now(), 0, null, null));

        List<TwitterEvent> twitterEvents = Filter.only(events, TwitterEvent.class);
        assertEquals(twitterEvents.size(), 1);
    }
}
