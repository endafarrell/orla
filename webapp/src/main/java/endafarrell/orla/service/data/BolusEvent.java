package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;


public class BolusEvent extends ManualEvent {
    public final static String EVENT_TEXT = "bolus";

    public final String type;
    public final Integer cmd;

    public BolusEvent(DateTime startTime, Number value, String type, Integer cmd) {
        super(startTime, EVENT_TEXT, value, Unit.IU);
        this.type = type;
        this.cmd = cmd;
    }

    protected BolusEvent(String id, DateTime startTime, Number value, String type, Integer cmd) {
        super(id, startTime, EVENT_TEXT, value, Unit.IU);
        this.type = type;
        this.cmd = cmd;
    }

    public static BaseEvent factory(String id, String kvvalue) {
        Struct struct = BaseEvent.struct(kvvalue);
        Double value = (struct.value == null) ? null : struct.value.doubleValue();
        ObjectNode json = struct.json;
        Integer cmd = json.get("cmd").asInt();
        String type = json.get("type").asText();
        return new BolusEvent(id, struct.startTime, value, type, cmd);
    }
}
