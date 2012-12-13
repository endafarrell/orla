package endafarrell.orla.service;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import endafarrell.orla.monitoring.OrlaMonitor;
import endafarrell.orla.service.data.parser.EndomondoHtmlHandler;
import endafarrell.orla.service.data.parser.SmartPixSaxHandler;
import endafarrell.orla.service.data.parser.TwitterMessageHandler;
import endafarrell.orla.service.data.persistence.Database;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
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
 * Skeleton service
 *
 * @author Enda Farrell
 * @since 20120-10-23
 */
public class Orla {
    final static SAXParserFactory saxParserFactory = WstxSAXParserFactory.newInstance();
    public static final String DATA_DIR = "/var/data/endafarrell/orla";

    final OrlaMonitor monitor;
    final SAXParser saxParser;
    final HashSet<Event> events;
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
        return events.size();
    }

    public int readTwitterMessages() {
        TwitterMessageHandler twitterMessageHandler = new TwitterMessageHandler();
        events.addAll(twitterMessageHandler.getNewMessages(events));
        database.save(events);
        return events.size();
    }

    public void writeEventsAsJson(final OutputStream outputStream) throws IOException {
        ArrayList<Event> eventsList = new ArrayList<Event>(events);
        Collections.sort(eventsList);
        //Collections.reverse(eventsList);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeEventsByDayAsJson(ServletOutputStream outputStream, boolean skipEventsList, int weeks) throws IOException {
        ArrayList<Event> eventsList = new ArrayList<Event>(events);
        if (weeks > 0) {
            eventsList = last(eventsList, Weeks.weeks(weeks));
        } else {
            Collections.sort(eventsList);
        }
        boolean addEvents = !skipEventsList;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
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
                    dayNode.put(Event.BOLUS_PLUS_BASAL, round(event.value.doubleValue(),2));
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
                dayNode.put("bolus", round(bolus, 1));
                dayNode.put("IU_10g", round(bolus * 10 / carbs,1));
                arrayNode.add(dayNode);
                // And reset
                dayNode = JsonNodeFactory.instance.objectNode();
                dayEvents = JsonNodeFactory.instance.arrayNode();
                if (addEvents) dayNode.put("events", dayEvents);
                dayNode.put("day", Event.dayFormat.format(event.date));
                dayNode.put("date", Event.dateFormat.format(event.date));
                if (addEvents) dayEvents.add(event.toJson());
                carbs = 0;
                bolus = 0d;
            }
            previous = event;
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException {
        ArrayList<Event> eventsList = new ArrayList<Event>(events);
        eventsList = last(eventsList, Weeks.weeks(weeks));
        eventsList = percentiles(eventsList, Event.Unit.mmol_L, lower, higher);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public static ArrayList<Event> last(ArrayList<Event> events, ReadablePeriod period) {
        Collections.sort(events);
        ArrayList<Event> last = new ArrayList<Event>(events.size());
        DateTime end = new DateTime(events.get(events.size() - 1).date);
        DateTime start = end.minus(period);
        Date startDate = start.toDate();
        for (Event e : events) {
            if (e.date.after(startDate)) {
                last.add(e);
            }
        }
        last.trimToSize();
        return last;
    }

    public static ArrayList<Event> percentiles(ArrayList<Event> events, Event.Unit unit, int lower, int higher) {
        if (unit != Event.Unit.mmol_L) {
            throw new NotImplementedException("Only Event.Unit.mmol_L is supported.");
        }

        // Get the raw values
        ArrayList<Double> values = new ArrayList<Double>(events.size());
        for (Event e : events) {
            if (e.unit == unit) {
                values.add(e.value.doubleValue());
            }
        }
        values.trimToSize();
        // Get the lower-th percentile
        Percentile percentile = new Percentile(lower);
        percentile.setData(todoubles(values));
        double lowerPercentile = percentile.evaluate();
        // Get the higher-th percentile
        percentile = new Percentile(higher);
        percentile.setData(todoubles(values));
        double higherPercentile = percentile.evaluate();

        // Now return the glucose readings between these percentiles
        ArrayList<Event> percentiles = new ArrayList<Event>(events.size());
        for (Event e : events) {
            if (e.unit == unit && e.value.doubleValue() > lowerPercentile && e.value.doubleValue() < higherPercentile) {
                percentiles.add(e);
            }
        }
        percentiles.trimToSize();
        return percentiles;
    }

    public static double[] todoubles(List<Double> source) {
        double[] doubles = new double[source.size()];
        int index = 0;
        for (Double d : source) {
            doubles[index++] = d;
        }
        return doubles;
    }
}
