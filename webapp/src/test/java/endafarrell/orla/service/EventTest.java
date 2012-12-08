package endafarrell.orla.service;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class EventTest {
    @Test
    public void testCompareTo() throws Exception {
        Date now = new Date();
        Date then = new Date(now.getTime() + 1000 * 60 * 60);
        Event first = new Event(now, Event.Source.SmartPix, "before meal", 8.6, Event.Unit.mmol_L);
        Event later = new Event(then, Event.Source.SmartPix, "after meal", 9.1, Event.Unit.mmol_L);
        Event tweet = new Event(then, Event.Source.Twitter, "out for a run", -1, Event.Unit.none);

        List<Event> eventList = new ArrayList<Event>();
        eventList.add(tweet);
        eventList.add(later);
        eventList.add(first);

        Collections.sort(eventList);

        Assert.assertTrue(eventList.get(0).equals(first));
        Assert.assertTrue(eventList.get(1).equals(later));
        Assert.assertTrue(eventList.get(2).equals(tweet));
    }

    public void testLotsOfEvents(int num) throws Exception {
        Random RND = new Random();
        RandomEnum<Event.Source> rndSource = new RandomEnum<Event.Source>(Event.Source.class);
        RandomEnum<Event.Unit> rndUnit = new RandomEnum<Event.Unit>(Event.Unit.class);
        List<Event> events = new ArrayList<Event>(num);
        Date now = new Date();
        for (int i = 0; i < num; i++) {
            events.add(new Event(new Date(now.getTime() + 1000 * RND.nextInt(60 * 60 * 24)),
                    rndSource.random(), "a string of some sort, of no interest", 0, rndUnit.random()));
        }
        Date created = new Date();
        Collections.sort(events);
        Date sorted = new Date();
        System.out.println(String.format("Created %s random Events in %s ms, sorted them in %s ms",
                num, created.getTime() - now.getTime(), sorted.getTime() - created.getTime()));

    }

    @Test
    public void test1000Events() throws Exception {
        testLotsOfEvents(1000);
    }

    @Test
    public void test1000000Events() throws Exception {
        testLotsOfEvents(1000000);
    }

    @Test
    public void test5MillionEvents() throws Exception {
        testLotsOfEvents(5 * 1000000);
    }
}
