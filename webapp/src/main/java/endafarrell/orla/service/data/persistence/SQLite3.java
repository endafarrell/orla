package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.data.Event;

import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

public class SQLite3 extends Database {

    public final String DATA_DIR;
    private final Connection connection;

    private static class SingletonHolder {
        public static SQLite3 INSTANCE(String dataDir) {
            return new SQLite3(dataDir);
        }
    }

    public static SQLite3 getInstance(String dataDir) {
        return SingletonHolder.INSTANCE(dataDir);
    }

    private SQLite3(final String datadir) {
        super();
        this.DATA_DIR = datadir;
        StringBuilder sql = new StringBuilder();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + datadir + "/sqlite3/orla.db");
            sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS events (")
                    .append("utc TEXT, ")
                    .append("source TEXT, ")
                    .append("text TEXT, ")
                    .append("value REAL, ")
                    .append("unit TEXT, ")
                    .append("_lastModified TEXT DEFAULT (datetime('now')), ")
                    .append("PRIMARY KEY (utc, source, unit));");
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sql.toString());

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(sql.toString(), e);
        }
    }

    /**
     * Save all the events, make sure there are no dupes.
     * <p/>
     * As the events are a set it has no duplicates. Therefore we
     * can safely start a transaction and try insert, catch the unique
     * key violation where the event is already in the database and
     * commit at the end.
     * <p/>
     * This doesn't throw exceptions. Perhaps it should.
     *
     * @param events The whole set of events to store, many of which
     *               may already be in the database.
     */
    @Override
    public void save(Collection<Event> events) {
        StringBuilder sql = new StringBuilder();
        try {
            Statement stmt = this.connection.createStatement();
            sql.append("BEGIN TRANSACTION; ");
            stmt.executeUpdate(sql.toString());
            for (Event event : events) {
                // Mostly for error logging
                sql = toSql(event);
                stmt.executeUpdate(sql.toString());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql.toString(), e);
        } finally {
            try {
                this.connection.createStatement().executeUpdate(" COMMIT;");
            } catch (SQLException e) {
                System.err.println("Could not commit " +
                        events.size() + " updates to the database." + e);
            }
        }
    }


    StringBuilder toSql(Event event) {
        return new StringBuilder("INSERT OR REPLACE INTO events (")
                .append("utc, source, text, value, unit")
                .append(") VALUES (\"")
                .append(dateFormat.format(event.date))
                .append("\", \"").append(event.source.toString())
                .append("\", \"").append(event.text)
                .append("\", ").append(event.value)
                .append(", \"").append(event.unit.toString())
                .append("\");");
    }

    public Set<Event> load() {
        StringBuilder sql = new StringBuilder();
        try {
            Statement stmt = this.connection.createStatement();
            sql.append("SELECT utc, source, text, value, unit from events;");
            ResultSet resultSet = stmt.executeQuery(sql.toString());
            HashSet<Event> events = new HashSet<Event>(5000);
            while (resultSet.next()) {
                Date date = new Date();
                try {
                    date = dateFormat.parse(resultSet.getString("utc"));
                } catch (ParseException ignored) {
                }
                double dValue = resultSet.getDouble("value");
                int iValue = resultSet.getInt("value");
                Number value = ((double) iValue == dValue) ? new Integer(iValue) : new Double(dValue);
                Event event = new Event(
                        date,
                        Event.Source.valueOf(resultSet.getString("source")),
                        resultSet.getString("text"),
                        value,
                        Event.Unit.valueOf(resultSet.getString("unit"))
                );
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            throw new IllegalStateException(sql.toString(), e);
        }
    }

    @Override
    public String getURL() {
        try {
        return this.connection.getMetaData().getURL();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
