package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Event;
import org.codehaus.jackson.node.ObjectNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Persistence {

    public abstract PersistenceResults saveToDB(List<ObjectNode> jsonList);
    public abstract HashSet<Event> loadFromDB();

    OrlaConfig config = OrlaConfig.getInstance();
}
