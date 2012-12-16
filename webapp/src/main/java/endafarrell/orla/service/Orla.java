package endafarrell.orla.service;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import endafarrell.orla.monitoring.OrlaMonitor;
import endafarrell.orla.service.data.parser.EndomondoHtmlHandler;
import endafarrell.orla.service.data.parser.SmartPixSaxHandler;
import endafarrell.orla.service.data.parser.TwitterMessageHandler;
import endafarrell.orla.service.data.persistence.Database;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.Weeks;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * Simple diabetes management service
 *
 * @author Enda Farrell
 * @since 20120-10-23
 */
public class Orla {
    final static SAXParserFactory saxParserFactory = WstxSAXParserFactory.newInstance();
    public static final String DATA_DIR = "/var/data/endafarrell/orla";

    final OrlaMonitor monitor;
    final SAXParser saxParser;
    HashSet<Event> events;
    final JsonFactory jsonFactory;
    final ObjectMapper objectMapper;
    final Database database;

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     * Pugh, Bill (November 16, 2008). "The Java Memory Model"
     * http://www.cs.umd.edu/~pugh/java/memoryModel/
     */
    private static class SingletonHolder {
        public static final Orla INSTANCE = new Orla();
    }

    public static Orla getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Orla() {
        this.monitor = OrlaMonitor.getInstance();
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();
        this.database = Database.getInstance();
        this.events = this.database.load();
        try {
            this.saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public ObjectNode config() {
        ObjectNode config = JsonNodeFactory.instance.objectNode();
        ObjectNode db = JsonNodeFactory.instance.objectNode();
        try {
            db.put("file", this.database.connection.getMetaData().getURL());
        } catch (SQLException e) {
            db.put("file", "error: " + e.getLocalizedMessage());
        }
        config.put("database", db);
        config.put("data_dir", DATA_DIR);
        return config;
    }

    /**
     * Reads the InputStream, assumes it's full of SmartPix XML and adds the parsed Events to the Events set.
     *
     * @param stream InputStream of SmartPix XML
     * @return The new number of (unique) elements in the Events set.
     */
    public int readSmartPixStream(InputStream stream) {
        try {
            SmartPixSaxHandler smartPixSaxHandler = new SmartPixSaxHandler();
            saxParser.parse(stream, smartPixSaxHandler);
            events.addAll(smartPixSaxHandler.getEvents());
            database.save(events);
            events = database.load();
            return events.size();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readEndomondoRuns() {
        EndomondoHtmlHandler endomondoHtmlHandler = new EndomondoHtmlHandler();
        events.addAll(endomondoHtmlHandler.getNewEvents(events));
        database.save(events);
        events = database.load();
        return events.size();
    }

    public int readTwitterMessages() {
        TwitterMessageHandler twitterMessageHandler = new TwitterMessageHandler();
        events.addAll(twitterMessageHandler.getNewMessages(events));
        database.save(events);
        events = database.load();
        return events.size();
    }

    public void writeEventsAsJson(final OutputStream outputStream) throws IOException {
        writeEventsAsJson(outputStream, -1);
    }

    /**
     * Writes the events as a JSON array, possibly time limited.
     * @param outputStream Where to write to
     * @param weeks The number of weeks, ending with the latest entries, of data to write. Values greater than zero
     *              will truncate
     * @throws IOException
     */
    public void writeEventsAsJson(final OutputStream outputStream, int weeks) throws IOException {
        ArrayList<Event> eventsList = getEventsList(weeks, false);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeAscentDecentByDayAsJson(OutputStream outputStream, int weeks) throws IOException {
        ArrayList<Event> eventsList = getEventsList(weeks, true);
        eventsList = Filter.only(eventsList, Event.Unit.mmol_L);
        Event previous = null;
        for (Event event : eventsList) {
            if (!event.sameDayAs(previous)) {
                System.err.println("Still haven't figured out how to aportion across midnight :-(");
            } else {

            }
        }
    }

    public void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException {
        ArrayList<Event> eventsList = getEventsList(weeks, false);
        boolean addEvents = !omitEventsList;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
        arrayNode.add(dayNode);
        ArrayNode dayEvents = JsonNodeFactory.instance.arrayNode();
        if (addEvents) dayNode.put("events", dayEvents);
        Integer carbs = 0;
        Double bolus = 0d;
        dayNode.put("day", Event.dayFormat.format(eventsList.get(0).date));
        dayNode.put("date", Event.dateFormat.format(eventsList.get(0).date));
        Event previous = null;
        for (Event event : eventsList) {
            if (event.sameDayAs(previous)) {
                if (Event.BOLUS_PLUS_BASAL.equals(event.text)) {
                    dayNode.put(Event.BOLUS_PLUS_BASAL, Convert.round(event.value.doubleValue(), 2));
                } else {
                    if (addEvents) dayEvents.add(event.toJson());
                }
                if (event.unit == Event.Unit.g) {
                    carbs += event.value.intValue();
                }
                if (event.unit == Event.Unit.IU && Event.BOLUS.equals(event.text)) {
                    bolus += event.value.doubleValue();
                }
            } else {
                // Not the same day, therefore decorate and add the dayNode to the arrayNode
                dayNode.put("carbs", carbs);
                dayNode.put("bolus", Convert.round(bolus, 1));
                dayNode.put("IU_10g", Convert.round(bolus * 10 / carbs, 1));

                // And reset
                dayNode = JsonNodeFactory.instance.objectNode();
                arrayNode.add(dayNode);
                dayEvents = JsonNodeFactory.instance.arrayNode();
                if (addEvents) dayNode.put("events", dayEvents);
                dayNode.put("day", Event.dayFormat.format(event.date));
                dayNode.put("date", Event.dateFormat.format(event.date));
                if (Event.BOLUS_PLUS_BASAL.equals(event.text)){
                    dayNode.put(Event.BOLUS_PLUS_BASAL, Convert.round(event.value.doubleValue(), 2));
                } else {
                    if (addEvents) dayEvents.add(event.toJson());
                }
                carbs = 0;
                bolus = 0d;
            }
            previous = event;
        }


        System.out.println((new Date()).toString() + ": " + eventsList.get(eventsList.size()-1).toString());
        System.out.println((new Date()).toString() + "| " + arrayNode.get(arrayNode.size()-1).toString());
        outputStream.write(arrayNode.toString().getBytes());
    }

    private ArrayList<Event> getEventsList(int weeks, boolean includePreceding) {
        ArrayList<Event> eventsList = new ArrayList<Event>(events);
        if (weeks > 0) {
            eventsList = Filter.last(eventsList, Weeks.weeks(weeks), includePreceding);
        } else {
            Collections.sort(eventsList);
        }
        return eventsList;
    }

    public void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException {
        ArrayList<Event> eventsList = getEventsList(weeks, false);
        eventsList = Filter.only(eventsList, Event.Unit.mmol_L);
        eventsList = Filter.percentiles(eventsList, lower, higher);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

}
