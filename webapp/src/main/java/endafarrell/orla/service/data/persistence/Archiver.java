package endafarrell.orla.service.data.persistence;

import java.io.InputStream;

public abstract class Archiver extends Persistence {
    public abstract PersistenceResults archive(String filename, InputStream inputStream);
}
