package endafarrell.orla.service.data;

import endafarrell.orla.service.OrlaDateTimeFormat;
import endafarrell.orla.service.OrlaObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public class HourlyStats implements OrlaObject {
    final DateTime dateTime;
    final int carbs;
    final double timeWeightedMean;
    final ObjectNode json;

    public HourlyStats(final DateTime dateTime,
                       final int carbs,
                       final double timeWeightedMean) {
        this.dateTime = dateTime;
        this.carbs = carbs;
        this.timeWeightedMean = timeWeightedMean;

        this.json = JsonNodeFactory.instance.objectNode();
        json.put("date", OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(dateTime));
        json.put("hour", dateTime.getHourOfDay());
        json.put("carbs", carbs);
        json.put("meanBG", timeWeightedMean);
    }

    @Override
    public String toString() {
        return "HourlyStats{" +
                "dateTime=" + dateTime +
                ", carbs=" + carbs +
                ", timeWeightedMean=" + timeWeightedMean +
                ", json=" + json +
                '}';
    }

    public JsonNode toJson() {
        return json;
    }
}
