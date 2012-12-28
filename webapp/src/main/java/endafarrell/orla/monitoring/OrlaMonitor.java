package endafarrell.orla.monitoring;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

public class OrlaMonitor {
    public enum TimeScale {
        MILLI,
        NANO
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     * Pugh, Bill (November 16, 2008). "The Java Memory Model"
     * http://www.cs.umd.edu/~pugh/java/memoryModel/
     */
    private static class SingletonHolder {
        public static final OrlaMonitor INSTANCE = new OrlaMonitor();
    }

    public static OrlaMonitor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private OrlaMonitor() {
        this.durations = new HashMap<String, ArrayList<TimeDuration>>();
    }

    private HashMap<String, ArrayList<TimeDuration>> durations;

    public long numCalls() {
        long calls = 0;
        for (String path : this.durations.keySet()) {
            ArrayList<TimeDuration> tds = this.durations.get(path);
            calls += tds.size();
        }
        return calls;
    }


    public void dumpAllData() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public int numPaths() {
        return this.durations.size();
    }

    public TimeDuration totalDuration() {
        TimeDuration duration = new TimeDuration();
        for (String path : this.durations.keySet()) {
            ArrayList<TimeDuration> tds = this.durations.get(path);
            for (TimeDuration td : tds) {
                duration.add(td);
            }
        }
        return duration;
    }

    public void recordResponseTime(String path, long duration, TimeScale scale) {
        if (!this.durations.containsKey(path)) {
            this.durations.put(path, new ArrayList<TimeDuration>());
        }
        ArrayList<TimeDuration> tds = this.durations.get(path);
        tds.add(new TimeDuration(DateTime.now(), duration, scale));
    }

    public class TimeDuration {

        private DateTime date;
        private long duration;
        private TimeScale scale;

        public TimeDuration(DateTime date, long duration, TimeScale scale) {
            this.date = date;
            this.duration = duration;
            this.scale = scale;
        }

        public TimeDuration() {
            this.date = DateTime.now();
            this.duration = 0;
            this.scale = TimeScale.NANO;
        }

        public TimeDuration add(TimeDuration that) {
            if (this.scale == that.scale) {
                this.duration += that.duration;
                return this;
            } else {
                long thisInNano = this.duration;
                if (this.scale == TimeScale.MILLI) {
                    thisInNano = thisInNano * 1000 * 1000;
                }
                long thatInNano = that.duration;
                if (that.scale == TimeScale.MILLI) {
                    thatInNano = thatInNano * 1000 * 1000;
                }
                this.scale = TimeScale.NANO;
                this.duration = thisInNano + thatInNano;
                return this;
            }

        }

        public String toString() {
            if (this.scale == TimeScale.MILLI) {
                return String.format("%d ms", this.duration);
            } else {
                return String.format("%d ns", this.duration);
            }
        }

        public String toMsString() {
            if (this.scale == TimeScale.MILLI) {
                return String.format("%d ms", this.duration);
            } else {
                return String.format("%d ms", this.duration / 1000 / 1000);
            }
        }
    }
}
