package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.OrlaConfig;
import endafarrell.orla.service.OrlaDateTimeFormat;
import endafarrell.orla.service.data.Event;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import java.io.*;
import java.util.HashSet;
import java.util.List;

public class FileSystemArchiver extends Archiver {

    @Override
    public PersistenceResults archive(String filename, InputStream inputStream) {
        if (filename == null) throw new IllegalArgumentException("filename must not be null");
        if (inputStream == null) throw new IllegalArgumentException("inputStream must not be null");
        try {

            FileWriter fileWriter = new FileWriter(archiveFilename(filename));
            IOUtils.copy(inputStream, fileWriter);
            IOUtils.closeQuietly(fileWriter);
            File smartPix = new File(archiveFilename(filename));
            PersistenceResults results = new PersistenceResults(true);
            results.setArchiveAbsolutePath(smartPix.getAbsolutePath());
            results.setArchiveByteSize(smartPix.getTotalSpace());
            return results;
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PersistenceResults saveToDB(List<ObjectNode> jsonList) {
        throw new NotImplementedException();
    }

    @Override
    public HashSet<Event> loadFromDB() {
        throw new NotImplementedException();
    }

    String archiveFilename(String partFilename) throws IOException {
        String archiveRoot = OrlaConfig.getInstance().fileArchiveLocation;
        File archiveFileDir = new File(archiveRoot + "/" + partFilename);
        if (!archiveFileDir.exists()) {
            FileUtils.forceMkdir(archiveFileDir);
        }
        return archiveFileDir.getAbsolutePath() + "/"
                        + OrlaDateTimeFormat.ARCHIVER_yyyyMMddTHHmmss.print(DateTime.now());

    }
}
