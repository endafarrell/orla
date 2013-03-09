package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;


public class PumpBolusEvent extends PumpEvent {
    public final static String EVENT_TEXT = "bolus";

    public final String type;
    public final Integer cmd;

    public PumpBolusEvent(DateTime startTime, Number value, String type, Integer cmd) {
        super(startTime, EVENT_TEXT, value, Unit.IU);
        this.type = type;
        this.cmd = cmd;
    }

    protected PumpBolusEvent(String id, DateTime startTime, Number value, String type, Integer cmd) {
        super(id, startTime, EVENT_TEXT, value, Unit.IU);
        this.type = type;
        this.cmd = cmd;
    }

    public static BaseEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        ObjectNode json = struct.json;
        Integer cmd = json.get("cmd").asInt();
        String type = json.get("type").asText();
        return new PumpBolusEvent(id, struct.startTime, value, type, cmd);
    }
}
