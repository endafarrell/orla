package endafarrell.orla.service;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import com.google.common.collect.Sets;
import endafarrell.healthgraph4j.*;
import endafarrell.orla.OrlaException;
import endafarrell.orla.monitoring.OrlaMonitor;
import endafarrell.orla.service.data.*;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import endafarrell.orla.service.data.persistence.FileSystemArchiver;
import endafarrell.orla.service.data.persistence.SQLite3;
import endafarrell.orla.service.processor.HealthGraphProcessor;
import endafarrell.orla.service.processor.ProcessResults;
import endafarrell.orla.service.processor.SmartpixProcessor;
import endafarrell.orla.service.processor.TwitterProcessor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

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
 * @since 2012-10-23
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
    private HealthGraph healthGraph;


    public static synchronized Orla getInstance() {
        if (OrlaImpl.INSTANCE == null) {
            try {
                OrlaImpl.INSTANCE = new OrlaImpl();
            } catch (ConfigurationException e) {
                throw new ExceptionInInitializerError(e);
            } catch (HealthGraphException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        return INSTANCE;
    }

    private OrlaImpl() throws ConfigurationException, HealthGraphException {
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
                // Read my own config files
        PropertiesConfiguration properties =
                new PropertiesConfiguration("/var/data/endafarrell/healthgraph4j/config/healthgraph4j.properties");
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setClientID(properties.getString("ClientID"));
        configurationBuilder.setClientSecret(properties.getString("ClientSecret"));
        configurationBuilder.setAuthorizationURL(properties.getString("AuthorizationURL"));
        configurationBuilder.setAccessTokenURL(properties.getString("AccessTokenURL"));
        configurationBuilder.setDeAuthorizationURL(properties.getString("DeAuthorizationURL"));
        configurationBuilder.setCallbackURL("http://localhost:8080/orla/api/home/healthgraph");
        configurationBuilder.setHttpsProxyHost(properties.getString("https.proxyHost"));
        configurationBuilder.setHttpsProxyPort(properties.getString("https.proxyPort"));

        Configuration configuration = configurationBuilder.build();

        // With the configuration, create a factory and then a new HealthGraph
        HealthGraphFactory factory = new HealthGraphFactory(configuration);
        healthGraph = factory.getInstance();

    }


    public List<Event> getEvents() {
        System.out.println("»OrlaImpl.getEvents()");
        return getEventsList(null, null, true);
    }

    public ProcessResults readSmartPix(String fileName, Part part) {
        System.out.println("»OrlaImpl.readSmartPix()");
        SmartpixProcessor processor = new SmartpixProcessor(this);
        processor.setInput(fileName, part);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
    }

    public ProcessResults readTwitterMessages() {
        System.out.println("»OrlaImpl.readTwitterMessages()");
        TwitterProcessor processor = new TwitterProcessor(this);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;
    }

    public ProcessResults readHealthgraphFitnessActivities() {
        System.out.println("»OrlaImpl.readHealthgraphFitnessActivities()");
        HealthGraphProcessor processor = new HealthGraphProcessor(this);
        ProcessResults results = processor.process();
        events = database.loadFromDB();
        return results;

    }

    public HealthGraph getHealthGraphClient() {
        System.out.println("»OrlaImpl.getHealthGraphClient()");
        return this.healthGraph;
    }

    public Database getDatabase() {
        System.out.println("»OrlaImpl.getDatabase()");
        return this.database;
    }

    public Archiver getArchiver() {
        System.out.println("»OrlaImpl.getArchiver()");
        return this.archiver;
    }

    public OrlaConfig getConfig() {
        System.out.println("»OrlaImpl.getConfig()");
        return this.config;
    }


    public void writeEventsAsJson(final OutputStream outputStream, DateTime from, DateTime to) throws IOException {
        System.out.println("»OrlaImpl.writeEventsAsJson(outputStream,"+from+","+to+")");
        List<Event> eventsList = getEventsList(from, to, false);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeDailyStatsAsJson(OutputStream outputStream, DateTime from, DateTime to) throws IOException {
        System.out.println("»OrlaImpl.writeDailyStatsAsJson(outputStream,"+from+","+to+")");
        List<Event> eventsList = getEventsList(from, to, true);
        List<DailyStats> dailyStats = Reducer.dailyStats(eventsList);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (DailyStats dailyStat : dailyStats) {
            arrayNode.add(dailyStat.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeEventsByDayAsJson(OutputStream outputStream, DateTime from, DateTime to) throws IOException {
        System.out.println("»OrlaImpl.writeEventsByDayAsJson(outputStream,"+from+","+to+")");
        List<Event> eventsList = getEventsList(from, to, false);

        if (eventsList == null) return;

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
        arrayNode.add(dayNode);
        ArrayNode dayEvents = JsonNodeFactory.instance.arrayNode();
        dayNode.put("events", dayEvents);

        Integer carbs = 0;
        Double bolus = 0d;
        Event previous = null;

        for (Event event : eventsList) {
            if (event.sameDayAs(previous)) {
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.getText())) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.getValue().doubleValue(), 2));
                } else {
                    dayEvents.add(event.toJson());
                }
                if (event.getUnit() == Unit.g) {
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
                dayNode.put("events", dayEvents);
                dayNode.put("day", OrlaDateTimeFormat.PRETTY_DAY_EEE.print(event.getStartTime()));
                dayNode.put("date", OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(event.getStartTime()));
                if (BaseEvent.BOLUS_PLUS_BASAL.equals(event.getText())) {
                    dayNode.put(BaseEvent.BOLUS_PLUS_BASAL, Convert.round(event.getValue().doubleValue(), 2));
                } else {
                    dayEvents.add(event.toJson());
                }
                carbs = 0;
                bolus = 0d;
            }
            previous = event;
        }

        outputStream.write(arrayNode.toString().getBytes());
    }

    private List<Event> getEventsList(DateTime from, DateTime to, boolean includePreceding) {
        List<Event> eventsList = new ArrayList<Event>(events);
        if (from != null && to != null) {
            eventsList = Filter.subList(eventsList, from, to, includePreceding);
        } else {
            Collections.sort(eventsList);
        }
        return eventsList;
    }

    public void writeGlucoseReadings(OutputStream outputStream, DateTime from, DateTime to) throws IOException {
        System.out.println("»OrlaImpl.writeGlucoseReadings(outputStream,"+from+","+to+")");
        List<Event> eventsList = getEventsList(from, to, false);
        eventsList = Filter.only(eventsList, Unit.mmol_L);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Event event : eventsList) {
            arrayNode.add(event.toJson());
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public void writeGlucoseOverlays(OutputStream outputStream, DateTime from, DateTime to) throws IOException {
        System.out.println("»OrlaImpl.writeGlucoseOverlays(outputStream,"+from+","+to+")");
        Set<String> requiredFields = Sets.newHashSet(Event.STARTTIME, "value");
        List<Event> eventsList = getEventsList(from, to, true);
        List<BloodGlucoseEvent> bGsList = Filter.only(eventsList, BloodGlucoseEvent.class);
        bGsList = Reducer.insertMidnights(bGsList);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode dayNode = JsonNodeFactory.instance.objectNode();
        arrayNode.add(dayNode);
        ArrayNode events = JsonNodeFactory.instance.arrayNode();
        dayNode.put("bGs", events);
        BloodGlucoseEvent previous = bGsList.remove(0);
        for (BloodGlucoseEvent current : bGsList) {
            if (current.sameDayAs(previous)) {
                events.add(current.toJson(requiredFields));
            } else {
                // We _ALSO_ put this new (midnight) in the previous
                events.add(current.toJson(requiredFields));
                dayNode = JsonNodeFactory.instance.objectNode();
                arrayNode.add(dayNode);
                events = JsonNodeFactory.instance.arrayNode();
                dayNode.put("bGs", events);
                events.add(current.toJson(requiredFields));
            }
            previous = current;
        }
        outputStream.write(arrayNode.toString().getBytes());
    }

    public String getHealthGraphAuthorisation() throws OrlaException {
        System.out.println("»OrlaImpl.getHealthGraphAuthorisation()");
        try {
            return healthGraph.authenticate(HealthGraph.AuthorisationMethod.OAuthCallback);
        } catch (HealthGraphException e) {
            throw new OrlaException(e);
        }
    }

    public void authenticate(String authenticationCode) throws OrlaException {
        System.out.println("»OrlaImpl.authenticate("+authenticationCode+")");
        try {
            healthGraph.authenticate(authenticationCode);
        } catch (HealthGraphException e) {
            throw new OrlaException(e);
        }
    }


}
