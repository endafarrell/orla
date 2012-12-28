package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.config.OrlaConfig;
import endafarrell.orla.service.data.BaseEvent;
import endafarrell.orla.service.data.Source;
import endafarrell.orla.service.data.Unit;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.*;

public class SQLite3 extends Database {
    static OrlaConfig config = OrlaConfig.getInstance();
    private final Connection connection;

    private static class SingletonHolder {
        public static SQLite3 INSTANCE= new SQLite3();
    }

    public static SQLite3 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private SQLite3() {
        super(config.getDatabaseConnectionString());
        StringBuilder sql = new StringBuilder();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.connectionString);
            sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS events (")
                    .append("utc TEXT, ")
                    .append("source TEXT, ")
                    .append("text TEXT, ")
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
    public void save(Collection<BaseEvent> events) {
        StringBuilder sql = new StringBuilder();
        try {
            Statement stmt = this.connection.createStatement();
            sql.append("BEGIN TRANSACTION; ");
            stmt.executeUpdate(sql.toString());
            for (BaseEvent event : events) {
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


    StringBuilder toSql(BaseEvent event) {
        return new StringBuilder("INSERT OR REPLACE INTO events (")
                .append("utc, source, text, value, unit")
                .append(") VALUES (\"")
                .append(dateFormat.print(event.startTime))
                .append("\", \"").append(event.source.toString())
                .append("\", \"").append(event.text)
                .append("\", ").append(event.value)
                .append(", \"").append(event.unit.toString())
                .append("\");");
    }

    public Set<BaseEvent> load() {
        StringBuilder sql = new StringBuilder();
        try {
            Statement stmt = this.connection.createStatement();
            sql.append("SELECT utc, source, text, value, unit from events;");
            ResultSet resultSet = stmt.executeQuery(sql.toString());
            HashSet<BaseEvent> events = new HashSet<BaseEvent>(5000);
            while (resultSet.next()) {
                DateTime date = DateTime.now();
                date = dateFormat.parseDateTime(resultSet.getString("utc"));
                double dValue = resultSet.getDouble("value");
                int iValue = resultSet.getInt("value");
                Number value = ((double) iValue == dValue) ? new Integer(iValue) : new Double(dValue);
                BaseEvent event = new BaseEvent(
                        date,
                        Source.valueOf(resultSet.getString("source")),
                        resultSet.getString("text"),
                        value,
                        Unit.valueOf(resultSet.getString("unit"))
                );
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            throw new IllegalStateException(sql.toString(), e);
        }
    }
}
