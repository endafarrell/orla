package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public class CarbEvent extends BaseEvent {
    public static String EVENT_TEXT = "carb";
    public final Integer D;
    public final String flg;

    public CarbEvent(DateTime startTime, Number value, Integer D, String flg) {
        super(startTime, Source.SmartPix, EVENT_TEXT, value, Unit.g);
        this.D = D;
        this.flg = flg;
    }

    protected CarbEvent(String id, DateTime startTime, Number value, Integer D, String flg) {
        super(id, startTime, Source.SmartPix, EVENT_TEXT, value, Unit.g);
        this.D = D;
        this.flg = flg;
    }

    static CarbEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);

        Integer value = (struct.value == null) ? null : struct.value.intValue();

        ObjectNode node = struct.json;
        Integer D = null;
        if (node.has("D")) {
            D = node.get("D").asInt();
        }
        String flg = null;
        if (node.has("flg")) {
            flg = node.get("flg").getTextValue();
        }
        return new CarbEvent(id, struct.startTime, value, D, flg);
    }
}
