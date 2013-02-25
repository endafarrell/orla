package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

public interface Event extends Comparable<Event> {
    String STARTTIME = "startTime";

    DateTime getStartTime();
    ObjectNode toJson();

    /**
     * True if the start time for this is in the same day as that.
     * @param that  The other event to compare to.
     * @return True iff the day in the current timezone of the start time for this and that is the same.
     */
    boolean sameDayAs(Event that);
    Unit getUnit();
    Source getSource();
    String getText();

    /**
     * Returns a number between 0 and 99 representing how far into the day the start time of the event is. Noon is
     * 50(%), 6pm is 75(%).
     * <p/>
     * There are 86400 seconds in a "normal day" (and this code does not do anything special with days having Daylight
     * Savings changes) so by dividing the number of seconds into the day by 864 we get a 0 <= pct < 100 number.
     *
     * @return an int between 0 and 99
     */
    int getTimeOfDayPercent();

    Number getValue();
}
