package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.data.Event;
import endafarrell.orla.service.data.EventFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.sql.*;
import java.util.HashSet;
import java.util.List;

public class SQLite3 extends Database {
    final Connection connection;

    private static class SingletonHolder {
        public static SQLite3 INSTANCE = new SQLite3();
    }

    public static SQLite3 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private SQLite3() {
        super();
        StringBuilder sql = new StringBuilder();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.connectionString);

            sql.append("CREATE TABLE IF NOT EXISTS ekv(")
                    .append("kvkey TEXT, ")
                    .append("clazz TEXT, ")
                    .append("kvvalue TEXT, ")
                    .append("_lastmodified TEXT DEFAULT (datetime('now')), ")
                    .append("PRIMARY KEY (kvkey));");
            Statement statement = connection.createStatement();
            statement.execute(sql.toString());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(sql.toString(), e);
        }
    }

    @Override
    public PersistenceResults saveToDB(List<ObjectNode> jsonList) {
        if (jsonList == null) return new PersistenceResults(false);

        PersistenceResults results = new PersistenceResults(true);
        results.setCountGiven(jsonList.size());
        int dbExecutions = 0;
        try {
            PreparedStatement prep = connection.prepareStatement("BEGIN TRANSACTION;");
            prep.execute();

            for (ObjectNode objectNode : jsonList) {
                String clazzName = objectNode.get("clazz").getTextValue();
                prep = connection.prepareStatement("INSERT OR REPLACE INTO ekv(kvkey, clazz, kvvalue) VALUES (?,?,?);");
                prep.setString(1, Database.kvKeyGenerator(objectNode));
                prep.setString(2, clazzName);
                prep.setString(3, objectNode.toString());
                dbExecutions += prep.executeUpdate();
            }
            prep = connection.prepareStatement("COMMIT");
            prep.execute();
            results.setCountPersists(dbExecutions);
        } catch (SQLException e) {
            return new PersistenceResults(false);
        }
        return results;
    }


    public HashSet<Event> loadFromDB() {
        try {
            HashSet<Event> events = new HashSet<Event>(50000);

            PreparedStatement statement = this.connection.prepareStatement("SELECT kvkey, clazz, kvvalue FROM ekv;");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String kvkey = resultSet.getString("kvkey");
                String clazz = resultSet.getString("clazz");
                String kvvalue = resultSet.getString("kvvalue");
                Event event = EventFactory.create(kvkey, clazz, kvvalue);
                events.add(event);
            }
            System.out.println("»SQLite3.loadFromDB() has " + events.size() + " events«");

            return events;
        } catch (SQLException e) {
            throw new IllegalStateException("\"SELECT kvkey, clazz, kvvalue FROM ekv;\" has thrown an exception.", e);
        }
    }
}
