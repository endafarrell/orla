package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.DTF;
import endafarrell.orla.service.data.BaseEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import java.io.*;
import java.util.List;
import java.util.Set;

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
    public Set<BaseEvent> loadFromDB() {
        throw new NotImplementedException();
    }

    String archiveFilename(String partFilename) {

        return config.getFileArchiveLocation() + "/" +
                DTF.ARCHIVER_yyyyMMddTHHmmss.print(DateTime.now()) +
                "-" + partFilename;
    }
}
