package endafarrell.orla.service.processor;


import endafarrell.healthgraph4j.FitnessActivityItem;
import endafarrell.healthgraph4j.HealthGraphException;
import endafarrell.healthgraph4j.api.HealthGraphList;
import endafarrell.orla.service.Convert;
import endafarrell.orla.service.Filter;
import endafarrell.orla.service.Orla;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Source;
import endafarrell.orla.service.data.SportEvent;
import endafarrell.orla.service.data.Unit;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;

public class HealthGraphProcessor extends ObtainingProcessor {
    public static PeriodFormatter sportPeriodFormat;

    static {
        sportPeriodFormat = new PeriodFormatterBuilder()
                .appendHours().appendSeparator(":")
                .minimumPrintedDigits(2)
                .appendMinutes().appendSeparator(":")
                .appendSeconds()
                .toFormatter();
    }

    public HealthGraphProcessor(Orla orla) {
        super(orla);
        events = new ArrayList<BaseEvent>(300);
    }

    @Override
    ProcessResults obtain() {
        int countUntilOverlap = 0;
        int payloadCount = 0;
        int totalCountForClass = -1;

        try {
            ArrayList<SportEvent> oldEvents = Filter.only(database.loadFromDB(), SportEvent.class);
            System.out.println(StringUtils.join(oldEvents, "\n"));
            System.out.println("Runkeeper oldEvents count is " + oldEvents.size());
            HealthGraphList<FitnessActivityItem> list = this.orla.getHealthGraphClient().getFitnessActivityList(true);
            System.out.println(list.size() + " fitness activities returned");
            for (FitnessActivityItem item : list) {
                DateTime date = item.getStartTime();
                Double value = Convert.round(item.getTotalDistance()/1000, 1);
                String text = "Running " + String.valueOf(value) + "km in "
                        + sportPeriodFormat.print(new Period((long) item.getDuration() * 1000));
                SportEvent event = new SportEvent(date, Source.RunKeeper, text, value, Unit.km);
                if (oldEvents.contains(event)) {
                    // OK: we're done! There's nothing new to see here.
                    System.out.println("Runkeeper events overlapped! Runkeeper count is " + events.size());
                    break;
                } else {
                    events.add(event);
                }
                countUntilOverlap++;
            }
        } catch (HealthGraphException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Runkeeper returning " + payloadCount + " and count until overlap of " + countUntilOverlap);
        return new ProcessResults(payloadCount, countUntilOverlap, totalCountForClass);

    }

    public ProcessResults process() {
        if (database == null) throw new IllegalStateException("database must be set before calling process");
        if (archiver == null) throw new IllegalStateException("archiver must be set before calling process");

        try {
            ProcessResults obtainResults = obtain();
            this.archiver.archive("runkeeper", eventsToInputSteam());
            this.database.saveToDB(eventsToJsonList());
            return obtainResults;
        } catch (RuntimeException e) {
            this.archiver.archive("runkeeper", eventsToInputSteam());
            this.database.saveToDB(eventsToJsonList());
            throw e;
        }
    }
}
