package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public class BloodGlucoseEvent extends BaseEvent {
    public static String EVENT_TEXT = "bG";
    public final Integer D;
    public final String flg;

    protected BloodGlucoseEvent(String id, DateTime startTime, Number value, Integer D, String flg) {
        super(id, startTime, Source.SmartPix, EVENT_TEXT, value, Unit.mmol_L);
        this.D = D;
        this.flg = flg;
    }

    public BloodGlucoseEvent(DateTime startTime, Number value, Integer D, String flg) {
        super(startTime, Source.SmartPix, EVENT_TEXT, value, Unit.mmol_L);
        this.D = D;
        this.flg = flg;
    }

    @Override
    /**
     * Adds a bG_color to the JSON based on the bG value;
     */
    public ObjectNode toJson() {
        ObjectNode json = super.toJson();
        double bG = 0;
        if (this.value != null) bG = this.value.doubleValue();
        String color;
        if (bG <= 0) {
            color = "blue";
        }else  if (bG < 4.5 && bG >= 0) {
            color = "red";
        } else if (bG < 5.0) {
            color = "yellow";
        } else if (bG < 9.0) {
            color = "green";
        } else if (bG < 16.0) {
            color = "grey";
        } else {
            color = "black";
        }
        json.put("bG_color", color);
        return json;
    }

    static BloodGlucoseEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        Double value = struct.value == null ? null : struct.value.doubleValue();

        Integer D = null;
        if (struct.json.has("D")) {
            D = struct.json.get("D").asInt();
        }
        String flg = null;
        if (struct.json.has("flg")) {
            flg = struct.json.get("flg").getTextValue();
        }

        return new BloodGlucoseEvent(id, struct.startTime, value, D, flg);

    }
}
