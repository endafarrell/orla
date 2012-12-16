package endafarrell.orla.service;

import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Filter {
    public static ArrayList<Event> last(final ArrayList<Event> events, final ReadablePeriod period, boolean includePreceding) {
        Collections.sort(events);
        ArrayList<Event> last = new ArrayList<Event>(events.size());
        DateTime end = new DateTime(events.get(events.size() - 1).date);
        DateTime start = end.minus(period);
        Date startDate = start.toDate();
        Event preceding = null;
        for (Event e : events) {
            if (e.date.after(startDate)) {
                last.add(e);
            } else {
                preceding = e;
            }
        }
        if (preceding != null && includePreceding) {
            last.add(0, preceding);
        }
        last.trimToSize();
        return last;
    }

    public static ArrayList<Event> last(final ArrayList<Event> events, final ReadablePeriod period) {
        return last(events, period, false);
    }

    public static ArrayList<Event> percentiles(ArrayList<Event> events, int lower, int higher) {
        // Get the raw values
        ArrayList<Double> values = new ArrayList<Double>(events.size());
        for (Event e : events) {
            values.add(e.value.doubleValue());
        }

        // Get the lower-th percentile
        Percentile percentile = new Percentile(lower);
        percentile.setData(Convert.toPrimitiveDoubleArray(values));
        double lowerPercentile = percentile.evaluate();
        // Get the higher-th percentile
        percentile = new Percentile(higher);
        percentile.setData(Convert.toPrimitiveDoubleArray(values));
        double higherPercentile = percentile.evaluate();

        // Now return the glucose readings between these percentiles
        ArrayList<Event> percentiles = new ArrayList<Event>(events.size());
        for (Event e : events) {
            if (e.value.doubleValue() > lowerPercentile && e.value.doubleValue() < higherPercentile) {
                percentiles.add(e);
            }
        }
        percentiles.trimToSize();
        return percentiles;
    }

    public static ArrayList<Event> only(ArrayList<Event> events, Event.Unit unit) {
        ArrayList<Event> eventList = new ArrayList<Event>(events.size());
        for (Event event : events) {
            if (event.unit == unit) {
                eventList.add(event);
            }
        }
        eventList.trimToSize();
        return eventList;
    }
}