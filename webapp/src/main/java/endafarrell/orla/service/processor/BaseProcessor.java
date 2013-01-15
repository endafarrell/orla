package endafarrell.orla.service.processor;

import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.persistence.Archiver;
import endafarrell.orla.service.data.persistence.Database;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;

public abstract class BaseProcessor {
    Database database;
    Archiver archiver;
    ArrayList<BaseEvent> events;
    OrlaConfig config = OrlaConfig.getInstance();

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setArchiver(Archiver archiver) {
        this.archiver = archiver;
    }

    ArrayList<ObjectNode> eventsToJsonList() {
        if (events == null) throw new IllegalStateException("eventsToJsonList must not be called before the events have been initialised");
        ArrayList<ObjectNode> jsonList = new ArrayList<ObjectNode>(events.size());
        for (BaseEvent event : events) {
            jsonList.add(event.toJson());
        }
        return jsonList;
    }

    public abstract ProcessResults process();

}
