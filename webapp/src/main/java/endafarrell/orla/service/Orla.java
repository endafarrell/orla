package endafarrell.orla.service;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import endafarrell.orla.monitoring.OrlaMonitor;
import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
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
public class Orla {
    private static Orla INSTANCE;
    final static OrlaConfig config = OrlaConfig.getInstance();

    final static SAXParserFactory saxParserFactory = WstxSAXParserFactory.newInstance();

    final OrlaMonitor monitor;
    final SAXParser saxParser;
    Set<BaseEvent> events;
    final JsonFactory jsonFactory;
    final ObjectMapper objectMapper;
    final Database database;
    final Archiver archiver;


    public static synchronized Orla getInstance() {
        if (Orla.INSTANCE == null) {
            Orla.INSTANCE = new Orla();
        }
        return INSTANCE;
    }

    private Orla() {
        this.monitor = OrlaMonitor.getInstance();
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();
        this.database = SQLite3.getInstance();
        this.archiver = new FileSystemArchiver();
        this.events = this.database.loadFromDB();
        try {
            this.saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public ObjectNode config() {
        ObjectNode configNode = JsonNodeFactory.instance.objectNode();
        ObjectNode db = JsonNodeFactory.instance.objectNode();
        db.put("file", config.databaseConnection);
        configNode.put("database", db);
        return configNode;
    }

    public ProcessResults readSmartPix(Part part) {
        SmartpixProcessor processor = new SmartpixProcessor();
        processor.setArchiver(this.archiver);
        processor.setDatabase(this.database);
        processor.setInput(part);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
    }

    public int readEndomondoRuns() {
        EndomondoHtmlHandler endomondoHtmlHandler = new EndomondoHtmlHandler();
        events.addAll(endomondoHtmlHandler.getNewEvents(events));
        //database.saveToDB(events);
                    if (1 > 2) throw new NotImplementedException();events = database.loadFromDB();
        return events.size();
    }

    public ProcessResults readTwitterMessages() {
        TwitterProcessor processor = new TwitterProcessor();
        processor.setArchiver(this.archiver);
        processor.setDatabase(this.database);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
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
        ArrayList<BaseEvent> eventsList = getEventsList(weeks, false);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (BaseEvent event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeAscentDecentByDayAsJson(OutputStream outputStream, int weeks) throws IOException {
        ArrayList<BaseEvent> eventsList = getEventsList(weeks, true);
        eventsList = Filter.only(eventsList, Unit.mmol_L);

        Map<String, List<Double>> dayAscDescs = new HashMap<String, List<Double>>(eventsList.size());
        long numDays = 0;
        long numBGs = 0;
        double totalBG = 0d;
        for (BaseEvent event : eventsList) {
            String date = DTF.PRETTY_yyyyMMdd.print(event.startTime);
            if (!dayAscDescs.containsKey(date)) {
                dayAscDescs.put(date, new ArrayList<Double>(10));
                numDays++;
            }
            dayAscDescs.get(date).add(event.value.doubleValue());
            numBGs++;
            totalBG = event.value.doubleValue();
        }
        double bGsPerDay = (double) numBGs / (double) numDays;
        double meanBG = totalBG / numBGs;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        BaseEvent previous = null;
        double ascDesc = 0d;
        for (BaseEvent event : eventsList) {
            if (previous == null) {

                ascDesc = Math.abs(event.value.doubleValue() - meanBG);

            } else {
                if (event.sameDayAs(previous)) {
                    ascDesc += Math.abs(event.value.doubleValue() - previous.value.doubleValue());
                } else {
                    ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
                    arrayNode.add(dayNode);
                    dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(previous.startTime));
                    dayNode.put("ascDesc", Convert.round(ascDesc * dayAscDescs.get(DTF.PRETTY_yyyyMMdd.print(previous.startTime)).size() / bGsPerDay, 1));

                    // Start over
                    ascDesc = Math.abs(event.value.doubleValue() - previous.value.doubleValue());
                }
            }
            previous = event;
        }
        if (previous != null) {
            // And now for the last day
            ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
            arrayNode.add(dayNode);
            dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(previous.startTime));
            dayNode.put("ascDesc", Convert.round(ascDesc * dayAscDescs.get(DTF.PRETTY_yyyyMMdd.print(previous.startTime)).size() / bGsPerDay, 1));
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeEventsByDayAsJson(OutputStream outputStream, boolean omitEventsList, int weeks) throws IOException {
        ArrayList<BaseEvent> eventsList = getEventsList(weeks, false);

        if (eventsList == null) return;

        boolean addEvents = !omitEventsList;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
        arrayNode.add(dayNode);
        ArrayNode dayEvents = JsonNodeFactory.instance.arrayNode();
        if (addEvents) dayNode.put("events", dayEvents);
        Integer carbs = 0;
        Double bolus = 0d;
        BaseEvent previous = null;
        for (BaseEvent event : eventsList) {
            if (event.sameDayAs(previous)) {
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.text)) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.value.doubleValue(), 2));
                } else {
                    if (addEvents) dayEvents.add(event.toJson());
                }
                if (event.unit == Unit.g && event.value != null) {
                    carbs += event.value.intValue();
                }
                if (event.unit == Unit.IU && BaseEvent.BOLUS.equals(event.text)) {
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
                dayNode.put("day", DTF.PRETTY_DAY_EEE.print(event.startTime));
                dayNode.put("date", DTF.PRETTY_yyyyMMdd.print(event.startTime));
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.text)) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.value.doubleValue(), 2));
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

    private ArrayList<BaseEvent> getEventsList(int weeks, boolean includePreceding) {
        ArrayList<BaseEvent> eventsList = new ArrayList<BaseEvent>(events);
        if (weeks > 0) {
            eventsList = Filter.last(eventsList, Weeks.weeks(weeks), includePreceding);
        } else {
            Collections.sort(eventsList);
        }
        return eventsList;
    }

    public void writeGlucoseReadings(OutputStream outputStream, int weeks) throws IOException {
        ArrayList<BaseEvent> eventsList = getEventsList(weeks, false);
        eventsList = Filter.only(eventsList, Unit.mmol_L);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (BaseEvent event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeGlucoseReadings(ServletOutputStream outputStream, int weeks, int lower, int higher) throws IOException {
        ArrayList<BaseEvent> eventsList = getEventsList(weeks, false);
        eventsList = Filter.only(eventsList, Unit.mmol_L);
        eventsList = Filter.percentiles(eventsList, lower, higher);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (BaseEvent event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

}
