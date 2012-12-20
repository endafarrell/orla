package endafarrell.orla.service.data.parser;

import endafarrell.orla.service.data.Event;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    public ArrayList<Event> getNewMessages(Set<Event> oldEvents) {
        ObjectMapper mapper = new ObjectMapper();
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
                System.out.println(mapper.writeValueAsString(dm));
            }
            return events;
        } catch (TwitterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JsonGenerationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
