package endafarrell.orla.service.data.parser;

import endafarrell.orla.service.Event;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Oddly, only the "BG" element is cared about. It has no text nor any children, only attributes. The attributes
 * found in a large sample were:
 * Carb,489
 * D,985
 * Dt,985
 * Evt,99
 * Flg,289
 * Ins1,1
 * Tm,985
 * Val,985
 * <BG Val="10.0" Dt="2012-11-03" Tm="14:26" D="1"/>
 * <BG Val="---" Dt="2012-11-03" Tm="13:17" Carb="30" D="1"/>
 * <BG Val="18.6" Dt="2012-11-03" Tm="13:03" Evt="74" D="1"/>
 * <BG Val="12.4" Dt="2012-11-03" Tm="11:57" Flg="M3" Carb="3" D="1"/>
 * <BG Val="9.5" Dt="2012-11-03" Tm="10:09" D="1"/>
 * <BG Val="3.6" Dt="2012-11-03" Tm="09:18" D="1"/>
 * <BG Val="21.2" Dt="2012-11-03" Tm="06:41" D="1"/>
 * <BG Val="6.6" Dt="2012-11-02" Tm="20:58" Flg="M1" Carb="20" D="1"/>
 * <
 */
public class SmartPixSaxHandler extends DefaultHandler {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm");
    private ArrayList<Event> events = new ArrayList<Event>(1500);

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if ("BG".equals(qName)) {
                String dt = attributes.getValue("Dt");
                String tm = attributes.getValue("Tm");
                Date date = dateFormat.parse(dt + tm);
                String val = attributes.getValue("Val");

                if (!"---".equals(val)) {
                    events.add(new Event(date, Event.Source.SmartPix, "bG", new Double(val), Event.Unit.mmol_L));
                }
                String carb = attributes.getValue("Carb");
                if (carb != null) {
                    events.add(new Event(date, Event.Source.SmartPix, "carb", new Integer(carb), Event.Unit.g));
                }
            }
        } catch (ParseException e) {
            throw new SAXException(e);
        }
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
