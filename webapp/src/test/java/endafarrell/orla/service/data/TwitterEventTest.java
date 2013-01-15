package endafarrell.orla.service.data;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class TwitterEventTest {
    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testJsonRountTripEquality() throws Exception {
        TwitterEvent twitterEvent = new TwitterEvent(DateTime.now(), "This is a test!");
        ObjectNode objectNode = twitterEvent.toJson();
        System.err.println(objectNode.toString());
        TwitterEvent twitterEvent1 = TwitterEvent.factory("id", objectNode.toString());
        assertTrue(twitterEvent.equals(twitterEvent1), twitterEvent.diff(twitterEvent1));
    }
}
