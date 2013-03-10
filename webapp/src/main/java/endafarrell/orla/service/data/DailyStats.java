package endafarrell.orla.service.data;

import endafarrell.orla.service.OrlaDateTimeFormat;
import endafarrell.orla.service.OrlaObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public class DailyStats implements OrlaObject {
    final DateTime dateTime;
    final int numReadings;
    final double simpleMean;
    final double timeWeightedMean;
    final double ascDesc;
    final ObjectNode json;

    public DailyStats(final DateTime dateTime,
                      final int numReadings,
                      final double simpleMean,
                      final double timeWeightedMean,
                      final double ascDesc) {
        this.dateTime = dateTime.withTimeAtStartOfDay();
        this.numReadings = numReadings;
        this.simpleMean = simpleMean;
        this.timeWeightedMean = timeWeightedMean;
        this.ascDesc = ascDesc;

        this.json = JsonNodeFactory.instance.objectNode();
        json.put("date", OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(dateTime));
        json.put("numReadings", numReadings);
        json.put("meanBG", timeWeightedMean);
        json.put("ascDesc", ascDesc);
    }

    @Override
    public String toString() {
        return "DailyStats{" +
                "dateTime=" + dateTime +
                ", numReadings=" + numReadings +
                ", simpleMean=" + simpleMean +
                ", timeWeightedMean=" + timeWeightedMean +
                ", ascDesc=" + ascDesc +
                '}';
    }

    public JsonNode toJson() {
        return json;
    }
}
