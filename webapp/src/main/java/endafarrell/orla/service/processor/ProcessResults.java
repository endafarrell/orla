package endafarrell.orla.service.processor;


import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;

public class ProcessResults {
    public final int payload;
    public final int overlap;
    public final int these;
    public final DateTime createdAt;

    public ProcessResults(int payload, int overlap, int these) {
        this.payload = payload;
        this.overlap = overlap;
        this.these = these;
        this.createdAt = DateTime.now(DateTimeZone.UTC);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            return "ProcessResults{" +
                    "payload=" + payload +
                    ", overlap=" + overlap +
                    ", these=" + these +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessResults results = (ProcessResults) o;

        if (overlap != results.overlap) return false;
        if (payload != results.payload) return false;
        if (these != results.these) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = payload;
        result = 31 * result + overlap;
        result = 31 * result + these;
        return result;
    }
}
