package endafarrell.orla;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;

public interface OrlaJsonWriter {
    void writeEventsAsJson(OutputStream outputStream, DateTime from, DateTime to) throws IOException;

    void writeDailyStatsAsJson(OutputStream outputStream, DateTime from, DateTime to) throws IOException;

    void writeEventsByDayAsJson(OutputStream outputStream, DateTime from, DateTime to) throws IOException;

    void writeGlucoseReadings(OutputStream outputStream, DateTime from, DateTime to) throws IOException;

    void writeGlucoseOverlays(OutputStream outputStream, DateTime from, DateTime to) throws IOException;
}
