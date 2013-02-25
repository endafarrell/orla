package endafarrell.orla.service.config;


import endafarrell.orla.api.home.SmartPixUploadServlet;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class OrlaConfig {
    private static OrlaConfig INSTANCE;
    static String PROPERTIES_LOCATION = "/var/data/endafarrell/orla/config/orla.properties";
    public final String databaseConnection;
    public final String fileArchiveLocation;
    public final  String twitterOAuthConsumerKey;
    public final  String twitterOAuthAccessTokenSecret;
    public final  String twitterOAuthConsumerSecret;
    public final  String twitterOAuthAccessToken;

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

            this.fileArchiveLocation = config.getString("smartpix.fileUploadLocation");
            if (this.fileArchiveLocation == null) {
                throw new IllegalStateException("The " + PROPERTIES_LOCATION + " properties files does not have an " +
                        "entry for smartpix.fileUploadLocation");
            }
            if (!SmartPixUploadServlet.FILE_UPLOAD_LOCATION.equals(this.fileArchiveLocation)) {
                throw new IllegalStateException("The " + PROPERTIES_LOCATION + " properties file entry for  " +
                        "smartpix.fileUploadLocation (" + this.fileArchiveLocation + ") /must/ match that of " +
                        "SmartPixUploadServlet.FILE_UPLOAD_LOCATION (" + SmartPixUploadServlet.FILE_UPLOAD_LOCATION +
                        ") due to the way \"@interface MultipartConfig\" works.");
            }
            this.twitterOAuthConsumerKey = config.getString("twitter_oauth_consumer_key");
            this.twitterOAuthAccessTokenSecret = config.getString("twitter_oauth_access_token_secret");
            this.twitterOAuthConsumerSecret = config.getString("twitter_oauth_consumer_secret");
            this.twitterOAuthAccessToken = config.getString("twitter_oauth_access_token");

            Field[] fields = this.getClass().getFields();
            ArrayList<String> configErrors = new ArrayList<String>(fields.length);
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                String fieldName = field.getName();
                Object thisFieldValue;
                try {
                    Field thisField = this.getClass().getField(fieldName);
                    thisFieldValue = thisField.get(this);
                    if (thisFieldValue == null) {
                        configErrors.add("The " + PROPERTIES_LOCATION + " properties files does not have an " +
                                                "entry for " + fieldName );
                    }

                } catch (Exception reflection) {
                    throw new RuntimeException(reflection);
                }
            }
            configErrors.trimToSize();
            if (configErrors.size() > 0) {
                throw new IllegalStateException(StringUtils.join(configErrors, ".\n"));
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);

        }
    }



    public String getFileArchiveLocation() {
        return fileArchiveLocation;
    }
}
