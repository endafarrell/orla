package endafarrell.orla.service;

import endafarrell.orla.service.data.DailyStats;
import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.processor.ProcessResults;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Part;

import java.util.List;

public class ReducerTest {
    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testDailyStats() throws Exception {
        Orla orla = OrlaImpl.getInstance();
        Part part = new FilePart("src/test/resources/SmartPix/G0030950.XML");
        ProcessResults first = orla.readSmartPix("test-G0030950.XML", part);
        List<Event> events = orla.getEvents();
        List<DailyStats> dailyStats = Reducer.dailyStats(events);

        for (DailyStats dailyStat: dailyStats) {
            System.out.println(dailyStat);
        }
    }
}
