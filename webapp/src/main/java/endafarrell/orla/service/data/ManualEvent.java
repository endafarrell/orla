package endafarrell.orla.service.data;

import org.joda.time.DateTime;

public class ManualEvent extends BaseEvent {
    public ManualEvent(DateTime startTime, String text, Number value, Unit unit) {
        super(startTime, Source.SmartPix, text, value, unit);
    }

    public ManualEvent(DateTime startTime, String text) {
        super(startTime, Source.Manual, text, null, Unit.none);
    }

    protected ManualEvent(String id, DateTime startTime, String text, Number value, Unit unit) {
        super(id, startTime, Source.Manual, text, value, unit);
    }

    public static BaseEvent factory(String id, String kvvalue) {
        Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        return new ManualEvent(id, struct.startTime, struct.text, value, struct.unit);
    }
}
