package endafarrell.orla.service.data;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class BaseEvent implements Event {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter dayFormat = DateTimeFormat.forPattern("EEE");

    public static final String BASAL = "basal";
    public static final String BOLUS = "bolus";
    public static final String BOLUS_PLUS_BASAL = "Bolus_plus_Basal";

    public final DateTime startTime;
    public DateTime endTime;
    public final Source source;
    public final String text;
    public final Number value;
    public final Unit unit;

    public BaseEvent(DateTime startTime, Source source, String text, Number value, Unit unit) {
        if (startTime == null) throw new IllegalArgumentException("startTime must not be null");

        this.startTime = startTime;
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
        int dateBased = this.startTime.compareTo(that.getStartTime());
        if (dateBased != 0) return dateBased;
        return this.unit.compareTo(that.getUnit());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEvent event = (BaseEvent) o;

        if (startTime != null ? !startTime.equals(event.startTime) : event.startTime != null) return false;
        if (source != event.source) return false;
        if (text != null ? !text.equals(event.text) : event.text != null) return false;
        if (unit != event.unit) return false;
        if (value != null ? !value.equals(event.value) : event.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public ObjectNode toJson() {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        try {
            objectNode.put("date", dateTimeFormat.print(startTime));
            objectNode.put("day", dayFormat.print(startTime));
            objectNode.put("time_pct", getTimeOfDayPercent());
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
            if (unit == Unit.mmol_L) {
                double mmol_L = value.doubleValue();
                String color = "black";
                if (16.5 > mmol_L) {
                    color = "grey";
                }
                if (9.0 > mmol_L) {
                    color = "green";
                }
                if (5.0 > mmol_L) {
                    color = "orange";
                }
                if (4.0 > mmol_L) {
                    color = "red";
                }
                if (0 > mmol_L) {
                    color = "blue";
                }
                objectNode.put("bG_color", color);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("This causes an ArrayIndexOutOfBoundsException: " + toString());
            e.printStackTrace();
        }
        return objectNode;
    }

    /**
     * True if the start time for this is in the same day as that. A null "that" will return false.
     *
     * @param that The other event to compare to.
     * @return True iff the day in the current timezone of the start time for this and that is the same.
     */
    public boolean sameDayAs(Event that) {
        return that != null && 0 == DateTimeComparator.getDateOnlyInstance().compare(this.startTime, that.getStartTime());
    }

    public Unit getUnit() {
        return unit;
    }

    public Source getSource() {
        return source;
    }

    public String getText() {
        return text;
    }

    /**
     * Returns a number between 0 and 99 representing how far into the day the start time of the event is. Noon is
     * 50(%), 6pm is 75(%).
     * <p/>
     * There are 86400 seconds in a "normal day" (and this code does not do anything special with days having Daylight
     * Savings changes) so by dividing the number of seconds into the day by 864 we get a 0 <= pct < 100 number.
     *
     * @return an int between 0 and 99
     */
    public int getTimeOfDayPercent() {
        return startTime.getSecondOfDay() / 864;
    }


}
