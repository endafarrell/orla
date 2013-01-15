package endafarrell.orla.service.data.parser;

import endafarrell.orla.service.data.TwitterEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

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

    public ArrayList<TwitterEvent> getNewMessages(List<TwitterEvent> oldEvents) {
        try {
            ResponseList<DirectMessage> dms = twitter.getDirectMessages();
            ArrayList<TwitterEvent> events = new ArrayList<TwitterEvent>(dms.size());
            for (DirectMessage dm : dms) {
                DateTime date = new DateTime(dm.getCreatedAt(), DateTimeZone.UTC);
                TwitterEvent message = new TwitterEvent(date, dm.getText());
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
