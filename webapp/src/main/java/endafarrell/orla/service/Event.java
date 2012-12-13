package endafarrell.orla.service;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Comparable<Event> {
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    public static final String BASAL = "basal";
    public static final String BOLUS = "bolus";
    public static final String BOLUS_PLUS_BASAL = "Bolus_plus_Basal";

    public enum Source {
        SmartPix,
        Twitter,
        Endomondo,
        Orla;
    }

    public enum Unit {
        /**
         * mmol/L, used for blood glucose readings
         */
        mmol_L,
        /**
         * grams, used for carbs
         */
        g,
        /**
         * U100 insulin units, used for injections
         */
        IU,
        /**
         * kilometers, used for running distances
         */
        km,
        /**
         * For when there's no value: eg tweets
         */
        none,
        /**
         * percent, used for HbA1C readings
         */
        pct
    }

    public final Date date;
    public final Source source;
    public final String text;
    public final Number value;
    public final Unit unit;

    public Event(Date date, Source source, String text, Number value, Unit unit) {
        if (date == null) throw new IllegalArgumentException("date must not be null");

        this.date = date;
        this.source = source;
        this.text = text;
        this.value = value;
        this.unit = unit;
    }

    /**
     * compareTo based on the dates. If by some odd reason the dates are exactly the same, the tie is broken by the
     * source.
     *
     * @param that The "other" being compared.
     * @return +1 if this event is newer than the "other"
     */
    public int compareTo(Event that) {
        int dateBased = this.date.compareTo(that.date);
        if (dateBased != 0) return dateBased;
        return this.unit.compareTo(that.unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (source != event.source) return false;
        if (text != null ? !text.equals(event.text) : event.text != null) return false;
        if (unit != event.unit) return false;
        if (value != null ? !value.equals(event.value) : event.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public ObjectNode toJson() {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("date", dateTimeFormat.format(date));
        objectNode.put("day", dayFormat.format(date));
        objectNode.put("time_pct", (int) ((date.getHours() * 60 + date.getMinutes()) / 14.4D));
        objectNode.put("source", source.toString());
        objectNode.put("text", StringEscapeUtils.escapeHtml(text));
        if (value == null) {
            objectNode.putNull("value");
        } else if (value instanceof Integer) {
            objectNode.put("value", value.intValue());
        } else {
            objectNode.put("value", value.doubleValue());
        }
        objectNode.put("unit", unit.toString());
        return objectNode;
    }

    public boolean sameDayAs(Event previous) {
        if (previous == null) return true;
        return dayFormat.format(date).equals(dayFormat.format(previous.date));
    }
}
