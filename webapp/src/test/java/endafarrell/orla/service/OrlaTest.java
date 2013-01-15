package endafarrell.orla.service;

import endafarrell.orla.service.processor.ProcessResults;
import org.testng.annotations.Test;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static org.testng.Assert.assertEquals;


public class OrlaTest {
    public class FilePart implements Part {
        final File file;

        public FilePart(String pathname) {
            this.file = new File(pathname);
        }

        public InputStream getInputStream() throws IOException {
            return new FileInputStream(this.file);
        }

        public String getContentType() {
            return "binary";
        }

        public String getName() {
            return this.file.getName();
        }

        public long getSize() {
            return this.file.length();
        }

        public void write(String fileName) throws IOException {
            return;
        }

        public void delete() throws IOException {
            this.file.delete();
        }

        public String getHeader(String name) {
            return null;
        }

        public Collection<String> getHeaders(String name) {
            return null;
        }

        public Collection<String> getHeaderNames() {
            return null;
        }
    }

    @Test
    public void testReadFile() throws Exception {
        Orla orla = Orla.getInstance();
        Part part1 = new FilePart("src/test/resources/SmartPix/G0030950.XML");
        Part part2 = new FilePart("src/test/resources/SmartPix/G0030950.XML");
        ProcessResults first = orla.readSmartPix(part1);
        ProcessResults second = orla.readSmartPix(part2);
        assertEquals(first, second);
    }

    @Test(dependsOnMethods = {"testReadFile"})
    public void testWriteEventsAsJson() throws Exception {
        Orla orla = Orla.getInstance();
        orla.writeEventsAsJson(System.out);
    }

}
