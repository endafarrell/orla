package endafarrell.orla.service;

import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.HealthGraphConsumer;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import endafarrell.orla.service.processor.ProcessResults;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.OutputStream;

public interface Orla extends HealthGraphConsumer {

    ProcessResults readSmartPix(Part part);

    ProcessResults readTwitterMessages();

    Database getDatabase();

    Archiver getArchiver();

    OrlaConfig getConfig();

//    void writeEventsAsJson(OutputStream outputStream) throws IOException;

    void writeEventsAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeAscentDecentByDayAsJson(OutputStream outputStream, int weeks) throws IOException;

    void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException;

    void writeGlucoseReadings(OutputStream outputStream, int weeks) throws IOException;

    void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException;

}
