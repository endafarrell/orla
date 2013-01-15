package endafarrell.orla.service.processor;


import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;

public class ProcessResults {
    public final int payloadCount;
    public final int countUntilOverlap;
    public final int totalCountForClass;
    public final DateTime createdAt;

    public ProcessResults(int payloadCount, int countUntilOverlap, int totalCountForClass) {
        this.payloadCount = payloadCount;
        this.countUntilOverlap = countUntilOverlap;
        this.totalCountForClass = totalCountForClass;
        this.createdAt = DateTime.now(DateTimeZone.UTC);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            return "ProcessResults{" +
                    "payloadCount=" + payloadCount +
                    ", countUntilOverlap=" + countUntilOverlap +
                    ", totalCountForClass=" + totalCountForClass +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessResults results = (ProcessResults) o;

        if (countUntilOverlap != results.countUntilOverlap) return false;
        if (payloadCount != results.payloadCount) return false;
        if (totalCountForClass != results.totalCountForClass) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = payloadCount;
        result = 31 * result + countUntilOverlap;
        result = 31 * result + totalCountForClass;
        return result;
    }
}
