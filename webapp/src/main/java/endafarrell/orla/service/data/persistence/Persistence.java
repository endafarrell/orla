package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import org.codehaus.jackson.node.ObjectNode;

import java.util.List;
import java.util.Set;

public abstract class Persistence {

    public abstract PersistenceResults saveToDB(List<ObjectNode> jsonList);
    public abstract Set<BaseEvent> loadFromDB();

    OrlaConfig config = OrlaConfig.getInstance();
}
