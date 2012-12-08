package endafarrell.orla.service.data.parser;

import endafarrell.orla.service.Event;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class TwitterMessageHandler {
    Twitter twitter;

    public TwitterMessageHandler() {
        ConfigurationBuilder endafarrell_med = new ConfigurationBuilder();
        endafarrell_med.setDebugEnabled(true)
                .setOAuthConsumerKey(System.getenv("ORLA_TWITTER_OAUTH_CONSUMER_KEY"))
                .setOAuthConsumerSecret(System.getenv("ORLA_TWITTER_OAUTH_CONSUMER_SECRET"))
                .setOAuthAccessToken(System.getenv("ORLA_TWITTER_OAUTH_ACCESS_TOKEN"))
                .setOAuthAccessTokenSecret(System.getenv("ORLA_TWITTER_OAUTH_ACCESS_TOKEN_SECRET"));

        TwitterFactory tf = new TwitterFactory(endafarrell_med.build());
        twitter = tf.getInstance();
    }

    public ArrayList<Event> getNewMessages(HashSet<Event> oldEvents) {
        try {
            ResponseList<DirectMessage> dms = twitter.getDirectMessages();
            ArrayList<Event> events = new ArrayList<Event>(dms.size());
            for (DirectMessage dm : dms) {
                Date date = dm.getCreatedAt();

                Event message = new Event(date, Event.Source.Twitter, dm.getText(), null, Event.Unit.none);
                if (oldEvents.contains(message)) {
                    // OK: we're done! There's nothing new to see here.
                    return events;
                } else {
                    events.add(message);
                }
            }
            return events;
        } catch (TwitterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
