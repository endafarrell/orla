package endafarrell.orla.service.data;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public class PumpBasalEvent extends PumpEvent {
    public final Integer cmd;
    public final Integer profile;

    protected PumpBasalEvent(String id, DateTime date, String text, Double cbrf, Integer profile, Integer cmd) {
        super(id, date, text, cbrf, Unit.IU);
        this.profile = profile;
        this.cmd = cmd;
    }

    public PumpBasalEvent(DateTime date, String text, Double cbrf, Integer profile, Integer cmd) {
        super(date, text, cbrf, Unit.IU);
        this.profile = profile;
        this.cmd = cmd;
    }

    public static PumpBasalEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        ObjectNode json = struct.json;
        Integer cmd = null;
        JsonNode cmdNode = json.get("cmd");
        if (cmdNode != null) {
            cmd = cmdNode.asInt();
        }

        // Always (?) there
        Integer profile = json.get("profile").asInt();

        Double cbrf = null;
        JsonNode cbrfNode = json.get("cbrf");
        if (cbrfNode != null){
            cbrf = cbrfNode.asDouble();
        }

        return new PumpBasalEvent(id, struct.startTime, struct.text, cbrf, profile, cmd);
    }
}
