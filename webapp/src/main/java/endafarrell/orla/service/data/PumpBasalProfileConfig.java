package endafarrell.orla.service.data;

import org.joda.time.DateTime;

public class PumpBasalProfileConfig extends PumpEvent {
    public PumpBasalProfileConfig(DateTime startTime, String text, Number value) {
        super(startTime, text, value, Unit.IU);
    }

    protected PumpBasalProfileConfig(String id, DateTime startTime, String text, Number value) {
        super(id, startTime, text, value, Unit.IU);
    }

    public static BaseEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();

        return new PumpBasalProfileConfig(id, struct.startTime, struct.text, value);
    }
}
