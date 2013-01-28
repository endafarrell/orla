package endafarrell.orla.service.processor;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import endafarrell.orla.service.Orla;
import endafarrell.orla.service.data.*;
import endafarrell.orla.service.data.persistence.PersistenceResults;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SmartpixProcessor extends ReceivingProcessor {
    final SAXParser saxParser;

    public SmartpixProcessor(Orla orla) {
        super(orla);
        try {
            this.saxParser = WstxSAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public ProcessResults process() {
            if (database == null) throw new IllegalStateException("database must be set before calling process");
            if (archiver == null) throw new IllegalStateException("archiver must be set before calling process");
            if (part == null) throw new IllegalStateException("input (Part) must be set before calling process");

        String filename = this.part.getName();
        InputStream inputStream = null;
        try {
            inputStream = this.part.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException("The input (Part) inputSteam threw an IOException", e);
        }
        PersistenceResults archiveResults = this.archiver.archive(filename, inputStream);
        SmartpixProcessor.SmartPixSaxHandler smartPixSaxHandler = new SmartpixProcessor.SmartPixSaxHandler();
        try {
            InputStream xmlStream = IOUtils.toBufferedInputStream(
                    new FileInputStream(archiveResults.getArchiveAbsolutePath()));

            this.saxParser.parse(xmlStream, smartPixSaxHandler);
            this.events = smartPixSaxHandler.getEvents();
            PersistenceResults dbResults = this.database.saveToDB(eventsToJsonList());

            return new ProcessResults(events.size(), dbResults.getCountPersists(), -1);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static class SmartPixSaxHandler extends DefaultHandler {
        private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-ddHH:mm");
        private ArrayList<BaseEvent> events = new ArrayList<BaseEvent>(1500);

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            try {
                if ("BG".equals(qName)) {
                    String dt = attributes.getValue("Dt");
                    String tm = attributes.getValue("Tm");
                    DateTime date = dateFormat.parseDateTime(dt + tm);
                    String val = attributes.getValue("Val");
                    Integer D = Integer.getInteger(attributes.getValue("D"));
                    String flg = attributes.getValue("flg");
                    if (!"---".equals(val)) {
                        events.add(new BloodGlucoseEvent(date, new Double(val), D, flg));
                    }
                    String carb = attributes.getValue("Carb");
                    if (carb != null) {
                        events.add(new CarbEvent(date, new Integer(carb), D, flg));
                    }
                } else if ("BOLUS".equals(qName)) {
                    // <BOLUS Dt="2012-12-08" Tm="13:32" type="Std" amount="5.30" cmd="1" />
                    String dt = attributes.getValue("Dt");
                    String tm = attributes.getValue("Tm");
                    if ("".equals(tm)) tm = "00:00";
                    DateTime date = dateFormat.parseDateTime(dt + tm);
                    Double amount = Double.valueOf(attributes.getValue("amount"));
                    String remark = attributes.getValue("remark");
                    String type = attributes.getValue("type");
                    Integer cmd = Integer.getInteger(attributes.getValue("cmd"));

                    if (remark == null) {
                        events.add(new PumpBolusEvent(date, amount, type, cmd));
                    } else {
                        events.add(new PumpDailyDoseEvent(dateFormat.parseDateTime(dt + "00:00"), remark, amount));
                    }
                } else if ("BASAL".equals(qName)) {
                    // <BASAL Dt="2012-12-01" Tm="16:34" cbrf="0.35" profile="2" TBRdec=" 50%" cmd="2" remark="dur 01:33 h" />
                    String dt = attributes.getValue("Dt");
                    String tm = attributes.getValue("Tm");
                    DateTime date = dateFormat.parseDateTime(dt + tm);
                    Double cbrf = Double.valueOf(attributes.getValue("cbrf"));
                    String remark = attributes.getValue("remark");
                    Integer profile = Integer.getInteger(attributes.getValue("profile"));
                    Integer cmd = Integer.getInteger(attributes.getValue("cmd"));
                    String tbrDec = attributes.getValue("TBRdec");
                    String tbrInc = attributes.getValue("TBRinc");
                    String text = (remark == null) ? null : remark.trim();
                    String tbr = (tbrDec == null) ? null : tbrDec.trim();
                    if (tbrInc != null) {
                        tbr = tbrInc.trim();
                    }
                    if (tbr != null) {
                        text = "TBR " + tbr + " " + remark;
                    }
                    events.add(new PumpBasalEvent(date, text, cbrf, profile, cmd));

                } else if ("EVENT".equals(qName)) {
                    String dt = attributes.getValue("Dt");
                    String tm = attributes.getValue("Tm");
                    DateTime date = dateFormat.parseDateTime(dt + tm);
                    String shortInfo = attributes.getValue("shortinfo");
                    String description = attributes.getValue("description");
                    if (shortInfo != null) {
                        description = description + " (" + shortInfo + ")";
                    }
                    events.add(new PumpEvent(date, description));

                } else {
                    System.err.println("Unhandled " + qName + asString(attributes));
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(qName + "(" + asString(attributes) + ") has problems", e);
            }
        }

        private String asString(Attributes attributes) {
            ArrayList<String> list = new ArrayList<String>(attributes.getLength());
            for (int index = 0; index < attributes.getLength(); index++) {
                list.add(attributes.getQName(index) + "=" + attributes.getValue(index));
            }
            return StringUtils.join(list, ",");
        }


        @Override
        public void warning(SAXParseException e) throws SAXException {
            e.printStackTrace();
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            e.printStackTrace();
        }

        public ArrayList<BaseEvent> getEvents() {
            events.trimToSize();
            return events;
        }
    }
}
