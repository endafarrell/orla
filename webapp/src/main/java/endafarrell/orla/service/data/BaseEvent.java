package endafarrell.orla.service.data;

import endafarrell.orla.service.OrlaDateTimeFormat;
import endafarrell.orla.service.OrlaObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public abstract class BaseEvent implements Event, OrlaObject {

    public static final String BOLUS = "bolus";
    public static final String BOLUS_PLUS_BASAL = "Bolus_plus_Basal";

    static ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
    }

    public final String id;
    public final DateTime startTime;
    public final Source source;
    public final String text;
    public final Number value;
    public final Unit unit;

    protected BaseEvent(String id, DateTime startTime, Source source, String text, Number value, Unit unit) {
        if (startTime == null) throw new IllegalArgumentException("startTime must not be null");

        this.id = id;
        this.startTime = startTime;
        this.source = source;
        this.text = text;
        this.value = value;
        this.unit = unit;
    }

    public BaseEvent(DateTime startTime, Source source, String text, Number value, Unit unit) {
        this(null, startTime, source, text, value, unit);
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
        int unitBased = this.unit.compareTo(that.getUnit());
        if (unitBased != 0) return unitBased;
        return this.source.compareTo(that.getSource());
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEvent event = (BaseEvent) o;

        // The second line returns false if the DateTimeZones for the two are different,
        // EVEN if the "when converted to UTC " times are the same
        //// if (startTime != null ? !startTime.equals(event.startTime) : event.startTime != null) return false;
        // In this app that is not good, so we ...
        if (DateTimeUtils.getInstantMillis(startTime) != DateTimeUtils.getInstantMillis(event.startTime)) return false;

        if (source != event.source) return false;
        if (text != null ? !text.equals(event.text) : event.text != null) return false;
        if (unit != event.unit) return false;
        if (value != null ? !value.equals(event.value) : event.value != null) return false;

        return true;
    }

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

    public Number getValue() {
        return value;
    }

    public ObjectNode toJson() {
        return toJson(null);
    }

    public ObjectNode toJson(Set<String> topLevelFields) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            String fieldName = field.getName();

            // If this field is not asked for, skip it
            if (topLevelFields != null && !topLevelFields.contains(fieldName)) continue;
            Object thisFieldValue;
            Class thisFieldClass;
            try {
                Field thisField = this.getClass().getField(fieldName);
                thisFieldValue = thisField.get(this);
                thisFieldClass = thisField.getType();
                if (thisFieldClass.equals(DateTime.class)) {
                    if (thisFieldValue != null) {
                        json.put(fieldName, OrlaDateTimeFormat.JSON_yyyyMMddHHmmssSSSZ.print((DateTime) thisFieldValue));
                    }
                } else if (thisFieldClass.equals(Number.class) && thisFieldValue != null) {
                    if (thisFieldValue instanceof Integer) {
                        json.put(fieldName, ((Integer) thisFieldValue).intValue());
                    } else {
                        json.put(fieldName, ((Double) thisFieldValue).doubleValue());
                    }
                } else {
                    json.put(fieldName, String.valueOf(thisFieldValue));
                }
            } catch (Exception reflection) {
                json.put(fieldName, reflection.getClass().getSimpleName());
            }
        }
        if (topLevelFields == null || topLevelFields.contains("day"))
            json.put("day", OrlaDateTimeFormat.PRETTY_DAY_EEE.print(startTime));
        if (topLevelFields == null || topLevelFields.contains("hhmm"))
            json.put("hhmm", OrlaDateTimeFormat.PRETTY_HHmm.print(startTime));
        if (topLevelFields == null || topLevelFields.contains("time_pct"))
            json.put("time_pct", getTimeOfDayPercent());
        if (topLevelFields == null || topLevelFields.contains("clazz"))
            json.put("clazz", this.getClass().getSimpleName());
        return json;
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

    public boolean sameHourAs(Event that){
        return sameDayAs(that) && this.startTime.getHourOfDay() == that.getStartTime().getHourOfDay();
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


//    public static BaseEvent factory(String kvkey, String clazz, String kvvalue) {
//        if (BloodGlucoseEvent.class.getSimpleName().equals(clazz)) {
//            return BloodGlucoseEvent.factory(kvkey, kvvalue);
//        } else if (CarbEvent.class.getSimpleName().equals(clazz)) {
//            return CarbEvent.factory(kvkey, kvvalue);
//        } else if (PumpBasalEvent.class.getSimpleName().equals(clazz)) {
//            return PumpBasalEvent.factory(kvkey, kvvalue);
//        } else if (PumpBolusEvent.class.getSimpleName().equals(clazz)) {
//            return PumpBolusEvent.factory(kvkey, kvvalue);
//        } else if (PumpDailyDoseEvent.class.getSimpleName().equals(clazz)) {
//            return PumpDailyDoseEvent.factory(kvkey, kvvalue);
//        } else if (PumpEvent.class.getSimpleName().equals(clazz)) {
//            return PumpEvent.factory(kvkey, kvvalue);
//        } else if (TwitterEvent.class.getSimpleName().equals(clazz)) {
//            return TwitterEvent.factory(kvkey, kvvalue);
//        } else {
//            throw new UnknownError("Class \"" + clazz + "\" is unknown.");
//        }
//    }

    public static class Struct {
        public final String id;
        public final DateTime startTime;
        public final Source source;
        public final String text;
        public final Number value;
        public final Unit unit;
        public final ObjectNode json;

        public Struct(String id, DateTime startTime, Source source, String text, Number value, Unit unit, ObjectNode json) {
            this.id = id;
            this.startTime = startTime;
            this.source = source;
            this.text = text;
            this.value = value;
            this.unit = unit;
            this.json = json;
        }
    }

    static Struct struct(String kvvalue) {
        JsonNode node;
        try {
            node = MAPPER.readTree(kvvalue);
        } catch (IOException e) {
            throw new IllegalArgumentException("Caused by this kvvalue:\n" + kvvalue + "\n", e);
        }
        String id = null;
        JsonNode idNode = node.get("id");
        if (idNode != null) {
            id = idNode.getTextValue();
        }
        DateTime startTime;
        try {
            startTime = OrlaDateTimeFormat.JSON_yyyyMMddHHmmssSSSZ.parseDateTime(node.get(Event.STARTTIME).getTextValue());
        } catch (IllegalArgumentException e) {
            System.err.println("Bad startTime in " + node.toString());
            throw e;
        }

        Source source = Source.valueOf(node.get("source").getTextValue());
        String text = node.get("text").getTextValue();
        Number value = node.get("value").getNumberValue();
        Unit unit = Unit.valueOf(node.get("unit").getTextValue());
        return new BaseEvent.Struct(id, startTime, source, text, value, unit, (ObjectNode) node);
    }

    public String diff(BaseEvent that) {
        return diff(that, false);
    }

    public String diff(BaseEvent that, boolean showAllFields) {
        ObjectNode diffs = JsonNodeFactory.instance.objectNode();
        ArrayNode clazz = JsonNodeFactory.instance.arrayNode();
        diffs.put("class", clazz);
        clazz.add(this.getClass().getSimpleName());
        clazz.add(that.getClass().getSimpleName());

        boolean dirty = false;
        if (!this.getClass().getSimpleName().equals(that.getClass().getSimpleName())) {
            dirty = true;
        }

        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            String fieldName = field.getName();
            String thatFieldValue;
            try {
                Field thatField = that.getClass().getField(fieldName);
                thatFieldValue = String.valueOf(thatField.get(that));
            } catch (Exception ignored) {
                thatFieldValue = ignored.getClass().getSimpleName();
            }

            String thisFieldValue;
            try {
                Field thisField = this.getClass().getField(fieldName);
                thisFieldValue = String.valueOf(thisField.get(this));
            } catch (Exception ignored) {
                thisFieldValue = ignored.getClass().getSimpleName();
            }

            if (!thisFieldValue.equals(thatFieldValue) || showAllFields) {
                dirty = true;
                ArrayNode diff = JsonNodeFactory.instance.arrayNode();
                diffs.put(field.getName(), diff);
                diff.add(thisFieldValue);
                diff.add(thatFieldValue);
            }
        }
        if (dirty || showAllFields) return diffs.toString();
        return null;
    }

    public String toString() {
        return this.toJson().toString();
    }
}
