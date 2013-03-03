package endafarrell.orla;

import java.io.IOException;
import java.io.OutputStream;

public interface OrlaJsonWriter {
    void writeEventsAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeDailyStatsAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException;

    void writeGlucoseReadings(OutputStream outputStream, int weeks) throws IOException;

    void writeGlucoseReadings(OutputStream outputStream, int weeks, boolean overlay) throws IOException;
}
