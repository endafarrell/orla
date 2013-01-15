package endafarrell.orla.service.data;

import org.joda.time.DateTime;


public class PumpDailyDoseEvent extends PumpEvent {
    public PumpDailyDoseEvent(DateTime dateTime, String remark, Double amount) {
        super(dateTime, remark, amount, Unit.IU);
    }

    protected PumpDailyDoseEvent(String id, DateTime dateTime, String remark, Double amount) {
        super(id, dateTime, remark, amount, Unit.IU);
    }

    public static BaseEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        return new PumpDailyDoseEvent(id, struct.startTime, struct.text, value);
    }
}
