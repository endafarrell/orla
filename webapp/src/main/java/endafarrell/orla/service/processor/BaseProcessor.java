package endafarrell.orla.service.processor;

import endafarrell.orla.service.Orla;
import endafarrell.orla.service.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseProcessor implements Processor {
    final Database database;
    final Archiver archiver;
    ArrayList<BaseEvent> events;
    final OrlaConfig config;
    final Orla orla;

    public BaseProcessor(Orla orla) {
        this.database = orla.getDatabase();
        this.archiver = orla.getArchiver();
        this.config = orla.getConfig();
        this.orla = orla;
    }

    List<ObjectNode> eventsToJsonList() {
        if (events == null) throw new IllegalStateException("eventsToJsonList must not be called before the events have been initialised");
        List<ObjectNode> jsonList = new ArrayList<ObjectNode>(events.size());
        for (BaseEvent event : events) {
            jsonList.add(event.toJson());
        }
        return jsonList;
    }

}
