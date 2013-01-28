package endafarrell.orla.service.processor;

import endafarrell.orla.service.Filter;
import endafarrell.orla.service.Orla;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.TwitterEvent;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;

public class TwitterProcessor extends ObtainingProcessor {

    final Twitter twitter;
    transient boolean stop = false;

    public TwitterProcessor(Orla orla) {
        super(orla);
        ConfigurationBuilder endafarrell_med = new ConfigurationBuilder();
        endafarrell_med.setDebugEnabled(true)
                .setOAuthConsumerKey(config.twitterOAuthConsumerKey)
                .setOAuthConsumerSecret(config.twitterOAuthConsumerSecret)
                .setOAuthAccessToken(config.twitterOAuthAccessToken)
                .setOAuthAccessTokenSecret(config.twitterOAuthAccessTokenSecret);

        TwitterFactory tf = new TwitterFactory(endafarrell_med.build());
        twitter = tf.getInstance();
        RateLimitStatusListener listener = new RateLimitStatusListener() {
            public void onRateLimitStatus(RateLimitStatusEvent event) { }

            public void onRateLimitReached(RateLimitStatusEvent event) {
                System.out.println("Twitter rateLimitReached!");
                stop = true;
            }
        };
        twitter.addRateLimitStatusListener(listener);
        events = new ArrayList<BaseEvent>(300);
    }

    @Override
    ProcessResults obtain() {
        int countUntilOverlap = 0;
        int payloadCount = 0;
        int totalCountForClass = -1;
        boolean overlapReached = false;

        try {
            ArrayList<TwitterEvent> oldEvents = Filter.only(database.loadFromDB(), TwitterEvent.class);
            System.out.println(StringUtils.join(oldEvents,"\n"));
            System.out.println("Twitter oldEvents count is " + oldEvents.size());

            for (int page = 1; page < 4 || overlapReached || stop; page++) {
                System.out.print("Twitter page " + page);
                Paging paging = new Paging(page, 100);
                if (stop) break;
                RateLimitStatus rateLimitStatus = twitter.getAPIConfiguration().getRateLimitStatus();
                int twitterLimitLimit = rateLimitStatus.getLimit();
                int twitterLimitRemaining = rateLimitStatus.getRemaining();
                if (twitterLimitLimit == 2) {
                    System.out.println("Twitter limit reached!");
                    stop = true;
                    break;
                }
                int twitterLimitSecondsUntilReset = rateLimitStatus.getResetTimeInSeconds();
                System.out.println("Twitter limit/remaining/seconds: " + twitterLimitLimit + "/" + twitterLimitRemaining + "/" + twitterLimitSecondsUntilReset);
                ResponseList<DirectMessage> dms = twitter.getDirectMessages(paging);

                payloadCount += dms.size();
                System.out.print(". Twitter payload count is now " + payloadCount);
                System.out.println(". Events count is now " + events.size());

                for (DirectMessage dm : dms) {
                    DateTime date = new DateTime(dm.getCreatedAt(), DateTimeZone.UTC);
                    TwitterEvent message = new TwitterEvent(date, dm.getText());
                    System.out.println("new?: " + message);
                    if (oldEvents.contains(message)) {
                        // OK: we're done! There's nothing new to see here.
                        overlapReached = true;
                        System.out.println("Twitter events overlapped! Twitter count is " + events.size());
                        break;
                    } else {
                        events.add(message);
                    }
                    countUntilOverlap++;
                }
                if (overlapReached) break;
            }

        } catch (TwitterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("Twitter returning " + payloadCount + " and count until overlap of " + countUntilOverlap);
        return new ProcessResults(payloadCount, countUntilOverlap, totalCountForClass);
    }

    public ProcessResults process() {
        if (database == null) throw new IllegalStateException("database must be set before calling process");
        if (archiver == null) throw new IllegalStateException("archiver must be set before calling process");

        try {
            ProcessResults obtainResults = obtain();
            this.archiver.archive("dms-to-endafarrell_med", eventsToInputSteam());
            this.database.saveToDB(eventsToJsonList());
            return obtainResults;
        } catch (RuntimeException e) {
            this.archiver.archive("dms-to-endafarrell_med", eventsToInputSteam());
            this.database.saveToDB(eventsToJsonList());
            throw e;
        }
    }

}
