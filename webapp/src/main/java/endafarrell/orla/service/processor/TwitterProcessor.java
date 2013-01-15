package endafarrell.orla.service.processor;

import endafarrell.orla.service.Filter;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.TwitterEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;


public class TwitterProcessor extends ObtainingProcessor {

    final Twitter twitter;

    public TwitterProcessor() {
        ConfigurationBuilder endafarrell_med = new ConfigurationBuilder();
        endafarrell_med.setDebugEnabled(true)
                .setOAuthConsumerKey(config.twitterOAuthConsumerKey)
                .setOAuthConsumerSecret(config.twitterOAuthConsumerSecret)
                .setOAuthAccessToken(config.twitterOAuthAccessToken)
                .setOAuthAccessTokenSecret(config.twitterOAuthAccessTokenSecret);

        TwitterFactory tf = new TwitterFactory(endafarrell_med.build());
        twitter = tf.getInstance();
        events = new ArrayList<BaseEvent>(300);
    }

    @Override
    ProcessResults obtain() {
        int countUntilOverlap = 0;
        int payloadCount;
        int totalCountForClass = -1;
        try {
            ResponseList<DirectMessage> dms = twitter.getDirectMessages();
            payloadCount = dms.size();
            ArrayList<TwitterEvent> oldEvents = Filter.only(database.loadFromDB(), TwitterEvent.class);

            for (DirectMessage dm : dms) {
                DateTime date = new DateTime(dm.getCreatedAt(), DateTimeZone.UTC);
                TwitterEvent message = new TwitterEvent(date, dm.getText());
                if (oldEvents.contains(message)) {
                    // OK: we're done! There's nothing new to see here.
                    break;
                } else {
                    events.add(message);
                }
                countUntilOverlap++;
            }

        } catch (TwitterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new ProcessResults(payloadCount, countUntilOverlap, totalCountForClass);
    }

    @Override
    public ProcessResults process() {
        if (database == null) throw new IllegalStateException("database must be set before calling process");
        if (archiver == null) throw new IllegalStateException("archiver must be set before calling process");

        ProcessResults obtainResults = obtain();
        this.archiver.archive("dms-to-endafarrell_med", eventsToInputSteam());
        this.database.saveToDB(eventsToJsonList());
        return obtainResults;
    }

}
