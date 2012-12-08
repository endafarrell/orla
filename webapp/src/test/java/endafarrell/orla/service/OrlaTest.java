package endafarrell.orla.service;

import org.testng.annotations.Test;

import java.io.FileInputStream;

import static org.testng.Assert.assertEquals;


public class OrlaTest {


    @Test
    public void testReadFile() throws Exception {
        Orla orla = Orla.getInstance();
        int first = orla.readSmartPixStream(new FileInputStream("src/test/resources/SmartPix/G0030950.XML"));
        int second = orla.readSmartPixStream(new FileInputStream("src/test/resources/SmartPix/G0030950.XML"));
        assertEquals(first, second);
    }

    @Test(dependsOnMethods = {"testReadFile"})
    public void testWriteEventsAsJson() throws Exception {
        Orla orla = Orla.getInstance();
        orla.writeEventsAsJson(System.out);
    }

}
