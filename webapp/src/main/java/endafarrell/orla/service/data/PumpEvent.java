package endafarrell.orla.service.data;

import org.joda.time.DateTime;

public class PumpEvent extends BaseEvent {
    public PumpEvent(DateTime startTime, String text, Number value, Unit unit) {
        super(startTime, Source.SmartPix, text, value, unit);
    }

    public PumpEvent(DateTime startTime, String text) {
        super(startTime, Source.SmartPix, text, null, Unit.none);
    }

    protected PumpEvent(String id, DateTime startTime, String text, Number value, Unit unit) {
        super(id, startTime, Source.SmartPix, text, value, unit);
    }

    public static BaseEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        return new PumpEvent(id, struct.startTime, struct.text, value, struct.unit);
    }
}
