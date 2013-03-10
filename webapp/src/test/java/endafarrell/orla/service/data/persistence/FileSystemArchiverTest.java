package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.OrlaConfig;
import endafarrell.orla.service.OrlaDateTimeFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

public class FileSystemArchiverTest {

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testArchive() throws Exception {

    }

    @Test
    public void testSaveToDB() throws Exception {

    }

    @Test
    public void testLoadFromDB() throws Exception {

    }

    @Test
    public void okToCreateSubdirs() throws Exception {
        String archiveRoot = OrlaConfig.getInstance().fileArchiveLocation;
        InputStream inputStream= new ByteArrayInputStream("This is a test file".getBytes());
        File archiveFileDir = new File(archiveRoot + "/test");
        if (!archiveFileDir.exists()) {
            FileUtils.forceMkdir(archiveFileDir);
        }
        FileWriter fileWriter = new FileWriter(archiveFileDir.getAbsolutePath() + "/"
                        + OrlaDateTimeFormat.ARCHIVER_yyyyMMddTHHmmss.print(DateTime.now()));
        IOUtils.copy(inputStream, fileWriter);
        IOUtils.closeQuietly(fileWriter);
    }
}
