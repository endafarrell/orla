package endafarrell.orla.service.config;


import endafarrell.orla.api.home.SmartPixUploadServlet;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class OrlaConfig {
    private static OrlaConfig INSTANCE;
    static String PROPERTIES_LOCATION = "/var/data/endafarrell/orla/config/orla.properties";
    String databaseConnection;
    String smartPixFileUploadLocation;

    public synchronized static OrlaConfig getInstance() {
        if (OrlaConfig.INSTANCE == null) {
            OrlaConfig.INSTANCE = new OrlaConfig();
        }
        return OrlaConfig.INSTANCE;
    }

    OrlaConfig() {
        try {
            Configuration config = new PropertiesConfiguration(PROPERTIES_LOCATION);
            this.databaseConnection = config.getString("database.connection");
            if (this.databaseConnection == null) {
                throw new IllegalStateException("The " + PROPERTIES_LOCATION + " properties file does not have an " +
                        "entry for database.connection");
            }
            this.smartPixFileUploadLocation = config.getString("smartpix.fileUploadLocation");
            if (this.smartPixFileUploadLocation == null) {
                throw new IllegalStateException("The " + PROPERTIES_LOCATION + " properties files does not have an " +
                        "entry for smartpix.fileUploadLocation");
            }
            if (!SmartPixUploadServlet.FILE_UPLOAD_LOCATION.equals(this.smartPixFileUploadLocation)) {
                throw new IllegalStateException("The " + PROPERTIES_LOCATION + " properties file entry for  " +
                        "smartpix.fileUploadLocation (" + this.smartPixFileUploadLocation + ") /must/ match that of " +
                        "SmartPixUploadServlet.FILE_UPLOAD_LOCATION (" + SmartPixUploadServlet.FILE_UPLOAD_LOCATION +
                        ") due to the way \"@interface MultipartConfig\" works.");
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);

        }
    }

    public String getDatabaseConnectionString() {
        return this.databaseConnection;
    }

    public static String getSmartPixFileUploadLocation() {
        return OrlaConfig.getInstance().smartPixFileUploadLocation;
    }
}
