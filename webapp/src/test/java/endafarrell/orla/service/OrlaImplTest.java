package endafarrell.orla.service;

import endafarrell.orla.api.OrlaHttpServlet;
import endafarrell.orla.service.processor.ProcessResults;
import org.testng.annotations.Test;

import javax.servlet.http.Part;

import static org.testng.Assert.assertEquals;


public class OrlaImplTest {

    @Test
    public void testReadFile() throws Exception {
        Orla orla = OrlaImpl.getInstance();
        Part part1 = new FilePart("src/test/resources/SmartPix/G0030950.XML");
        Part part2 = new FilePart("src/test/resources/SmartPix/G0030950.XML");
        ProcessResults first = orla.readSmartPix("test1-G0030950.XML",part1);
        ProcessResults second = orla.readSmartPix("test2-G0030950.XML", part2);
        assertEquals(first, second);
    }

    @Test(dependsOnMethods = {"testReadFile"})
    public void testWriteEventsAsJson() throws Exception {
        Orla orla = OrlaImpl.getInstance();
        orla.writeEventsAsJson(System.out, null, null);
    }

}
