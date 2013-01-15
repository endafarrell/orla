package endafarrell.orla.service.data.persistence;

import endafarrell.orla.service.config.OrlaConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.node.ObjectNode;

public abstract class Database extends Persistence {
    final String connectionString;

    public Database() {
        this(OrlaConfig.getInstance());
    }

    public Database(OrlaConfig config) {
        this.config = config;
        this.connectionString = config.databaseConnection;
    }

    public static String kvKeyGenerator(ObjectNode objectNode) {
        return DigestUtils.md5Hex(objectNode.toString());
    }
}
