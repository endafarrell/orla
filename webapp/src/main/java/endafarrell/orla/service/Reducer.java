package endafarrell.orla.service;


import endafarrell.orla.service.data.BloodGlucoseEvent;
import endafarrell.orla.service.data.DailyStats;
import endafarrell.orla.service.data.Event;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.math.stat.StatUtils.mean;
import static org.apache.commons.math.stat.StatUtils.sum;


public class Reducer {
    public static final double MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    public static List<DailyStats> dailyStats(List<Event> events) {
        List<BloodGlucoseEvent> bloodGlucoses = Filter.only(events, BloodGlucoseEvent.class);
        if (bloodGlucoses == null) return null;
        int bgOrigSize = bloodGlucoses.size();
        if (bgOrigSize < 2) return null;


        // So - we have something with which to work. Now - to make the calculation of the "area under the graph" of
        // the bloodGlucose readings, we want to add in midnights.
        bloodGlucoses = insertMidnights(bloodGlucoses);

        // Somewhere to store our stats
        List<DailyStats> dailyStats = new ArrayList<DailyStats>((int) (1.2 * (bloodGlucoses.size() - bgOrigSize)));

        // Now - reduce some numbers. These are what we want.
        int numReadings = 0;
        List<Double> bgs = new ArrayList<Double>();
        List<Double> areas = new ArrayList<Double>();
        double ascDesc = 0;

        BloodGlucoseEvent previous = bloodGlucoses.remove(0);
        for (BloodGlucoseEvent current : bloodGlucoses) {
            if (!current.sameDayAs(previous)) {
                dailyStats.add(
                        new DailyStats(previous.getStartTime(),
                                       numReadings,
                                       Convert.round(mean(Convert.todoubleArray(bgs))),
                                       Convert.round(sum(Convert.todoubleArray(areas))),
                                       Convert.round(ascDesc))
                );
                numReadings = 0;
                bgs = new ArrayList<Double>();
                areas = new ArrayList<Double>();
                ascDesc = 0;
            }
            numReadings++;
            bgs.add(current.getValue().doubleValue());
            areas.add(area(previous, current));
            ascDesc += Math.abs(current.getValue().doubleValue() - previous.getValue().doubleValue());

            // And finally - swap the previous.
            previous = current;
        }

        return dailyStats;
    }

    static double area(final BloodGlucoseEvent previous, final BloodGlucoseEvent current) {
        return area(previous.getStartTime().getMillis(), previous.getValue().doubleValue(),
                    current.getStartTime().getMillis(), current.getValue().doubleValue());
    }

    private static double area(final long w, final double nw, final long e, final double ne) {
        double rect = (e - w) * nw;
        double tri = 0.5 * (ne - nw) * (e - w);
        double poly = rect + tri;
        return poly / MILLIS_PER_DAY;
    }

    public static List<BloodGlucoseEvent> insertMidnights(final List<BloodGlucoseEvent> bloodGlucoses) {
        BloodGlucoseEvent previous = bloodGlucoses.remove(0);
        List<BloodGlucoseEvent> bgsIncMidnight = new ArrayList<BloodGlucoseEvent>((int) (1.5 * bloodGlucoses.size()));
        bgsIncMidnight.add(previous);
        for (BloodGlucoseEvent current : bloodGlucoses) {
            if (!current.sameDayAs(previous)) {
                bgsIncMidnight.add(interpolate(previous, current));
            }
            bgsIncMidnight.add(current);
            previous = current;
        }
        return bgsIncMidnight;
    }

    public static BloodGlucoseEvent interpolate(final BloodGlucoseEvent previous, final BloodGlucoseEvent current) {
        DateTime midnight = current.getStartTime().toDateMidnight().toDateTime();
        Double value = interpolate_y(
                previous.getStartTime().getMillis(), previous.getValue().doubleValue(),
                current.getStartTime().getMillis(), current.getValue().doubleValue(),
                midnight.getMillis()
        );
        return new BloodGlucoseEvent(midnight, value, current.D, current.flg);
    }

    /** y = y0 + (y1 - y0) * ((x - x0) / (x1 -x0)) */
    public static double interpolate_y(long x0, double y0, long x1, double y1, long x) {
        return Convert.round(y0 + (y1 - y0) * ((double) (x - x0) / (x1 - x0)));
    }
}
