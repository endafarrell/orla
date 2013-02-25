package endafarrell.orla;


import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface OrlaJsonWriter {
    void writeEventsAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeAscentDecentByDayAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException;

    void writeGlucoseReadings(OutputStream outputStream, int weeks) throws IOException;

    void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException;
}
