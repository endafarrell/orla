package endafarrell.orla.service.processor;

import endafarrell.orla.service.Orla;
import endafarrell.orla.service.data.BaseEvent;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class ObtainingProcessor extends BaseProcessor {

    public ObtainingProcessor(Orla orla) {
        super(orla);
    }

    abstract ProcessResults obtain();
    InputStream eventsToInputSteam() {
        if (events == null) throw new IllegalStateException("events must not be null before trying to archive them!");
        ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for (BaseEvent event : events) {
            json.add(event.toJson());
        }
        return new ByteArrayInputStream(json.toString().getBytes());
    }
}
