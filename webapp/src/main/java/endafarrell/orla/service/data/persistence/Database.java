package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.data.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;
import java.util.TimeZone;

public abstract class Database {

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Database() {
        Database.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public abstract void save(Collection<Event> events);
    public abstract Set<Event> load();
    public abstract String getURL();
}
