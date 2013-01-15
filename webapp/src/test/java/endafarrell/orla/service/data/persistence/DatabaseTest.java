package endafarrell.orla.service.data.persistence;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class DatabaseTest {

    @BeforeMethod
    public void setUp() throws Exception {
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testKvKeyGenerator() throws Exception {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        String on1 = Database.kvKeyGenerator(objectNode);
        objectNode.put("key", "value");
        String on2 = Database.kvKeyGenerator(objectNode);
        objectNode.put("key", "valuf");
        String on3 = Database.kvKeyGenerator(objectNode);
        assertNotNull(on1);
        assertNotNull(on2);
        assertNotNull(on3);
        assertNotEquals(on1, on2);
        assertNotEquals(on2, on3);
        assertNotEquals(on3, on1);
    }

    @Test
    public void testSave() throws Exception {

    }

    @Test
    public void testLoad1() throws Exception {

    }

    @Test
    public void testLoad2() throws Exception {

    }
}
