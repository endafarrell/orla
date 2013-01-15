package endafarrell.orla.service.data;


import org.joda.time.DateTime;

public class SportEvent extends BaseEvent {
    public SportEvent(DateTime startTime, Source source, String text, Number value, Unit unit) {
        super(startTime, source, text, value, unit);
    }

    protected SportEvent(String id, DateTime startTime, Source source, String text, Number value, Unit unit) {
        super(id, startTime, source, text, value, unit);
    }

    public static BaseEvent factory(String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        return new SportEvent(struct.id, struct.startTime, struct.source, struct.text, value, struct.unit);
    }
}
