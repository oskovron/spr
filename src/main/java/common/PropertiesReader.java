package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
//    private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

    private PropertiesReader() {}

    private static final String EXCEPTION_TEXT = "Error occurred during reading properties file: ";

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static Properties configs;

    public static String getProperty(final String propertyName) {
        synchronized (PropertiesReader.class) {
            if (configs == null) {
                try (final InputStream reader = PropertiesReader.class
                        .getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
                    final Properties properties = new Properties();
                    properties.load(reader);
                    configs = properties;
                } catch (IOException ex) {
                    throw new RuntimeException(EXCEPTION_TEXT + ex.getMessage());
                }
            }
        }
        final String systemProperty = System.getProperty(propertyName);
        var condition = configs.getProperty(propertyName, "Property Reader Fails");
        return systemProperty == null ? condition : systemProperty;
    }

    public static void setConfigProperty(String name, String value ) {
        configs.setProperty(name,value);
    }
}
