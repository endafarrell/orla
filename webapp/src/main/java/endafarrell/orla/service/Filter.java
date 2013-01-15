package endafarrell.orla.service;

import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Unit;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import java.util.*;

public final class Filter {
    public static ArrayList<BaseEvent> last(final ArrayList<BaseEvent> events, final ReadablePeriod period, boolean includePreceding) {
        if (events == null) return null;
        if (events.size() == 0) return null;
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

    @SuppressWarnings({"unchecked"})
    public static <T extends BaseEvent> ArrayList<T> only(Collection<BaseEvent> events, Class<T> clazz) {
        ArrayList<T> eventList = new ArrayList<T>(events.size());
        for (BaseEvent event : events) {
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

    Set<ObjectNode> getClassSubSet(Class clazz, Set<ObjectNode> objectNodeSet) {
        if (clazz == null) return objectNodeSet;
        if (objectNodeSet == null) throw new IllegalArgumentException("Null objectNodeSet is not allowed");
        String clazzName = clazz.getSimpleName();
        Set<ObjectNode> subset = new HashSet<ObjectNode>(objectNodeSet.size());
        for (ObjectNode objectNode : objectNodeSet) {
            if (objectNode.has("class") && clazzName.equals(objectNode.get("class").getTextValue())){
                subset.add(objectNode);
            }
        }
        return subset;
    }
}