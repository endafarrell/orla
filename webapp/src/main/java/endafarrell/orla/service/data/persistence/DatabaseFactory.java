package endafarrell.orla.service.data.persistence;

public class DatabaseFactory {
    public static Database getInstance(final String dataDir) {
        return SQLite3.getInstance(dataDir);
    }
}
