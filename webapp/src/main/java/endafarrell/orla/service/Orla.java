package endafarrell.orla.service;

import endafarrell.orla.OrlaJsonWriter;
import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.data.HealthGraphConsumer;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import endafarrell.orla.service.processor.ProcessResults;

import javax.servlet.http.Part;
import java.util.List;

public interface Orla extends
        HealthGraphConsumer,
        OrlaJsonWriter {

    ProcessResults readSmartPix(String fileName, Part part);

    ProcessResults readTwitterMessages();

    Database getDatabase();

    Archiver getArchiver();

    OrlaConfig getConfig();

    List<Event> getEvents();

}
