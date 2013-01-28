package endafarrell.orla.service;

import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.data.Unit;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import java.util.*;

public final class Filter {
    public static List<Event> last(final List<Event> events, final ReadablePeriod period, boolean includePreceding) {
        if (events == null) return null;
        if (events.size() == 0) return null;
        Collections.sort(events);
        ArrayList<Event> last = new ArrayList<Event>(events.size());
        DateTime end = new DateTime(events.get(events.size() - 1).getStartTime());
        DateTime start = end.minus(period);
        Event preceding = null;
        for (Event e : events) {
            if (e.getStartTime().isAfter(start)) {
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


    public static List<Event> percentiles(Collection<Event> events, int lower, int higher) {
        // Get the raw values
        List<Double> values = new ArrayList<Double>(events.size());
        for (Event e : events) {
            values.add(e.getValue().doubleValue());
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
            if (e.getValue().doubleValue() > lowerPercentile && e.getValue().doubleValue() < higherPercentile) {
                percentiles.add(e);
            }
        }
        percentiles.trimToSize();
        return percentiles;
    }

    public static List<Event> only(Collection<Event> events, Unit unit) {
        ArrayList<Event> eventList = new ArrayList<Event>(events.size());
        for (Event event : events) {
            if (event.getUnit() == unit) {
                eventList.add(event);
            }
        }
        eventList.trimToSize();
        return eventList;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Event> ArrayList<T> only(Collection<Event> events, Class<T> clazz) {
        ArrayList<T> eventList = new ArrayList<T>(events.size());
        for (Event event : events) {
            // There is a "noinspection unchecked" here which is used to suppress (in this IDE anyway)
            // compiler warnings - this time about Unchecked casts. Here you can see that we are checking
            // the canonical name of T which extends BaseEvent and of course that the Class<T> class is a
            // subclass of BaseEvent in the first place. So: we can suppress the warning as it has been
            // checked.
            if (event.getClass().getCanonicalName().equals(clazz.getCanonicalName())) {
                eventList.add((T) event);
            }
        }
        eventList.trimToSize();
        return eventList;
    }

}