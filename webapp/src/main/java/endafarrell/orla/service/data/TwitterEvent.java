package endafarrell.orla.service.data;


import org.joda.time.DateTime;

public class TwitterEvent extends BaseEvent {
    public TwitterEvent(DateTime startTime, String text) {
        super(startTime, Source.Twitter, text, null, Unit.none);
    }

    protected TwitterEvent(String id, DateTime startTime, String text) {
        super(id, startTime, Source.Twitter, text, null, Unit.none);
    }
    static TwitterEvent factory(String id, String kvvalue) {
        BaseEvent.Struct struct = BaseEvent.struct(kvvalue);
        return new TwitterEvent(id, struct.startTime, struct.text);

    }
}