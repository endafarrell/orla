package endafarrell.orla.service;

import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Unit;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import java.util.ArrayList;
import java.util.Collections;

public class Filter {
    public static ArrayList<BaseEvent> last(final ArrayList<BaseEvent> events, final ReadablePeriod period, boolean includePreceding) {
        Collections.sort(events);
        ArrayList<BaseEvent> last = new ArrayList<BaseEvent>(events.size());
        DateTime end = new DateTime(events.get(events.size() - 1).startTime);
        DateTime start = end.minus(period);
        BaseEvent preceding = null;
        for (BaseEvent e : events) {
            if (e.startTime.isAfter(start)) {
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

    public static ArrayList<BaseEvent> last(final ArrayList<BaseEvent> events, final ReadablePeriod period) {
        return last(events, period, false);
    }

    public static ArrayList<BaseEvent> percentiles(ArrayList<BaseEvent> events, int lower, int higher) {
        // Get the raw values
        ArrayList<Double> values = new ArrayList<Double>(events.size());
        for (BaseEvent e : events) {
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
        ArrayList<BaseEvent> percentiles = new ArrayList<BaseEvent>(events.size());
        for (BaseEvent e : events) {
            if (e.value.doubleValue() > lowerPercentile && e.value.doubleValue() < higherPercentile) {
                percentiles.add(e);
            }
        }
        percentiles.trimToSize();
        return percentiles;
    }

    public static ArrayList<BaseEvent> only(ArrayList<BaseEvent> events, Unit unit) {
        ArrayList<BaseEvent> eventList = new ArrayList<BaseEvent>(events.size());
        for (BaseEvent event : events) {
            if (event.unit == unit) {
                eventList.add(event);
            }
        }
        eventList.trimToSize();
        return eventList;
    }
}