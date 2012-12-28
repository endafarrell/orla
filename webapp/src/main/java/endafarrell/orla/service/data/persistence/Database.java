package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.data.BaseEvent;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;
import java.util.TimeZone;

public abstract class Database {

    public static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    final String connectionString;
    Database(final String connectionString) {
        Database.dateFormat.withZoneUTC();
        this.connectionString = connectionString;
    }

    public abstract void save(Collection<BaseEvent> events);
    public abstract Set<BaseEvent> load();
    public String getConnectionString() {
            return this.connectionString;
    };
}
