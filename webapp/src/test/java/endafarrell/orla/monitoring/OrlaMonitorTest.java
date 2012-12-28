package endafarrell.orla.monitoring;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class OrlaMonitorTest {
    @Test
    public void testGetInstanceType() throws Exception {
        Object orlaMonitor = OrlaMonitor.getInstance();
        assertTrue(orlaMonitor instanceof OrlaMonitor);
    }

    @Test
    public void testGetInstanceNotNull() throws Exception {
        OrlaMonitor orlaMonitor = OrlaMonitor.getInstance();
        assertNotNull(orlaMonitor);
    }


    @Test
    public void testNumCalls() throws Exception {

    }

    @Test
    public void testDumpAllData() throws Exception {

    }

    @Test
    public void testNumPaths() throws Exception {

    }

    @Test
    public void testTotalDuration() throws Exception {

    }

    @Test
    public void testRecordResponseTime() throws Exception {

    }
}
