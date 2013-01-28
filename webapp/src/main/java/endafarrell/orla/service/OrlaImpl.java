package endafarrell.orla.service;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import endafarrell.orla.monitoring.OrlaMonitor;
import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.data.Unit;
import endafarrell.orla.service.data.parser.EndomondoHtmlHandler;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import endafarrell.orla.service.data.persistence.FileSystemArchiver;
import endafarrell.orla.service.data.persistence.SQLite3;
import endafarrell.orla.service.processor.ProcessResults;
import endafarrell.orla.service.processor.SmartpixProcessor;
import endafarrell.orla.service.processor.TwitterProcessor;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.Weeks;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Simple diabetes management service
 *
 * @author Enda Farrell
 * @since 20120-10-23
 */
public class OrlaImpl implements Orla {
    private static OrlaImpl INSTANCE;

    final static SAXParserFactory saxParserFactory = WstxSAXParserFactory.newInstance();

    final OrlaMonitor monitor;
    final SAXParser saxParser;
    final JsonFactory jsonFactory;
    final ObjectMapper objectMapper;
    final Database database;
    final Archiver archiver;
    final OrlaConfig config;

    HashSet<Event> events;


    public static synchronized Orla getInstance() {
        if (OrlaImpl.INSTANCE == null) {
            OrlaImpl.INSTANCE = new OrlaImpl();
        }
        return INSTANCE;
    }

    private OrlaImpl() {
        this.monitor = OrlaMonitor.getInstance();
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();

        this.database = SQLite3.getInstance();
        this.archiver = new FileSystemArchiver();
        this.config = OrlaConfig.getInstance();

        this.events = this.database.loadFromDB();
        try {
            this.saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public ProcessResults readSmartPix(Part part) {
        SmartpixProcessor processor = new SmartpixProcessor(this);
        processor.setInput(part);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
    }

    public ProcessResults readTwitterMessages() {
        TwitterProcessor processor = new TwitterProcessor(this);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
    }

    public Database getDatabase() {
        return this.database;
    }

    public Archiver getArchiver() {
        return this.archiver;
    }

    public OrlaConfig getConfig() {
        return this.config;
    }


    public void writeEventsAsJson(final OutputStream outputStream) throws IOException {
        writeEventsAsJson(outputStream, -1);
    }

    /**
     * Writes the events as a JSON array, possibly time limited.
     *
     * @param outputStream Where to write to
     * @param weeks        The number of weeks, ending with the latest entries, of data to write. Values greater than zero
     *                     will truncate
     * @throws IOException
     */
    public void writeEventsAsJson(final OutputStream outputStream, int weeks) throws IOException {
        List<Event> eventsList = getEventsList(weeks, false);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeAscentDecentByDayAsJson(OutputStream outputStream, int weeks) throws IOException {
        List<Event> eventsList = getEventsList(weeks, true);
        eventsList = Filter.only(eventsList, Unit.mmol_L);

        Map<String, List<Double>> dayAscDescs = new HashMap<String, List<Double>>(eventsList.size());
        long numDays = 0;
        long numBGs = 0;
        double totalBG = 0d;
        for (Event event : eventsList) {
            String date = DTF.PRETTY_yyyyMMdd.print(event.getStartTime());
            if (!dayAscDescs.containsKey(date)) {
                dayAscDescs.put(date, new ArrayList<Double>(10));
                numDays++;
            }
            dayAscDescs.get(date).add(event.getValue().doubleValue());
            numBGs++;
            totalBG = event.getValue().doubleValue();
        }
        double bGsPerDay = (double) numBGs / (double) numDays;
        double meanBG = totalBG / numBGs;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        Event previous = null;
        double ascDesc = 0d;
        for (Event event : eventsList) {
            if (previous == null) {

                ascDesc = Math.abs(event.getValue().doubleValue() - meanBG);

            } else {
                if (event.sameDayAs(previous)) {
                    ascDesc += Math.abs(event.getValue().doubleValue() - previous.getValue().doubleValue());
                } else {
                    ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
                    arrayNode.add(dayNode);
                    dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(previous.getStartTime()));
                    dayNode.put("ascDesc", Convert.round(ascDesc * dayAscDescs.get(DTF.PRETTY_yyyyMMdd.print(previous.getStartTime())).size() / bGsPerDay, 1));

                    // Start over
                    ascDesc = Math.abs(event.getValue().doubleValue() - previous.getValue().doubleValue());
                }
            }
            previous = event;
        }
        if (previous != null) {
            // And now for the last day
            ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
            arrayNode.add(dayNode);
            dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(previous.getStartTime()));
            dayNode.put("ascDesc", Convert.round(ascDesc * dayAscDescs.get(DTF.PRETTY_yyyyMMdd.print(previous.getStartTime())).size() / bGsPerDay, 1));
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException {
        List<Event> eventsList = getEventsList(weeks, false);

        if (eventsList == null) return;

        boolean addEvents = !omitEventsList;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
        arrayNode.add(dayNode);
        ArrayNode dayEvents = JsonNodeFactory.instance.arrayNode();
        if (addEvents) dayNode.put("events", dayEvents);
        Integer carbs = 0;
        Double bolus = 0d;
        Event previous = null;
        for (Event event : eventsList) {
            if (event.sameDayAs(previous)) {
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.getText())) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.getValue().doubleValue(), 2));
                } else {
                    if (addEvents) dayEvents.add(event.toJson());
                }
                if (event.getUnit() == Unit.g && event.getUnit() != null) {
                    carbs += event.getValue().intValue();
                }
                if (event.getUnit() == Unit.IU && BaseEvent.BOLUS.equals(event.getText())) {
                    bolus += event.getValue().doubleValue();
                }
            } else {
                // Not the same day, therefore decorate and add the dayNode to the arrayNode
                dayNode.put("carbs", carbs);
                dayNode.put("bolus", Convert.round(bolus, 1));

                // And reset
                dayNode = JsonNodeFactory.instance.objectNode();
                arrayNode.add(dayNode);
                dayEvents = JsonNodeFactory.instance.arrayNode();
                if (addEvents) dayNode.put("events", dayEvents);
                dayNode.put("day", DTF.PRETTY_DAY_EEE.print(event.getStartTime()));
                dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(event.getStartTime()));
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.getText())) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.getValue().doubleValue(), 2));
                } else {
                    if (addEvents) dayEvents.add(event.toJson());
                }
                carbs = 0;
                bolus = 0d;
            }
            previous = event;
        }

        outputStream.write(arrayNode.toString().getBytes());
    }

    private List<Event> getEventsList(int weeks, boolean includePreceding) {
        List<Event> eventsList = new ArrayList<Event>(events);
        if (weeks > 0) {
            eventsList = Filter.last(eventsList, Weeks.weeks(weeks), includePreceding);
        } else {
            Collections.sort(eventsList);
        }
        return eventsList;
    }

    public void writeGlucoseReadings(OutputStream outputStream, int weeks) throws IOException {
        List<Event> eventsList = getEventsList(weeks, false);
        eventsList = Filter.only(eventsList, Unit.mmol_L);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException {
        List<Event> eventsList = getEventsList(weeks, false);
        eventsList = Filter.only(eventsList, Unit.mmol_L);
        eventsList = Filter.percentiles(eventsList, lower, higher);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

}
