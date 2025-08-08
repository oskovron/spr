package common;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Configuration reader using Apache Commons Configuration.
 * Thread-safe singleton pattern for reading application properties.
 */
public final class PropertiesReader {

    private static final Logger LOGGER = LogManager.getLogger(PropertiesReader.class);
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String EXCEPTION_TEXT = "Error occurred during reading properties file: ";
    
    private static volatile Configuration configuration;
    private static final Object lock = new Object();

    private PropertiesReader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets a property value from configuration.
     * System properties take precedence over file properties.
     *
     * @param propertyName the name of the property to retrieve
     * @return the property value, or null if not found
     */
    public static String getProperty(final String propertyName) {
        if (propertyName == null || propertyName.trim().isEmpty()) {
            LOGGER.warn("Property name is null or empty");
            return null;
        }

        // Check system property first (takes precedence)
        String systemProperty = System.getProperty(propertyName);
        if (systemProperty != null) {
            LOGGER.debug("Retrieved system property: {} = {}", propertyName, systemProperty);
            return systemProperty;
        }

        // Get from configuration file
        String configValue = getConfiguration().getString(propertyName);
        if (configValue != null) {
            LOGGER.debug("Retrieved config property: {} = {}", propertyName, configValue);
            return configValue;
        }

        LOGGER.warn("Property not found: {}", propertyName);
        return null;
    }

    /**
     * Gets a property value with a default fallback.
     *
     * @param propertyName the name of the property to retrieve
     * @param defaultValue the default value if property is not found
     * @return the property value or default value
     */
    public static String getProperty(final String propertyName, final String defaultValue) {
        String value = getProperty(propertyName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets an integer property value.
     *
     * @param propertyName the name of the property to retrieve
     * @param defaultValue the default value if property is not found or not a number
     * @return the integer property value or default value
     */
    public static int getIntProperty(final String propertyName, final int defaultValue) {
        String value = getProperty(propertyName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Property {} is not a valid integer: {}", propertyName, value);
            }
        }
        return defaultValue;
    }

    /**
     * Sets a configuration property (for testing purposes).
     *
     * @param name the property name
     * @param value the property value
     */
    public static void setConfigProperty(final String name, final String value) {
        if (name == null || name.trim().isEmpty()) {
            LOGGER.warn("Cannot set property with null or empty name");
            return;
        }
        
        getConfiguration().setProperty(name, value);
        LOGGER.debug("Set configuration property: {} = {}", name, value);
    }

    /**
     * Gets the configuration instance, initializing it if necessary.
     * Thread-safe singleton pattern.
     *
     * @return the Configuration instance
     */
    private static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (lock) {
                if (configuration == null) {
                    configuration = initializeConfiguration();
                }
            }
        }
        return configuration;
    }

    /**
     * Initializes the configuration from the properties file.
     *
     * @return the initialized Configuration
     */
    private static Configuration initializeConfiguration() {
        try {
            Configurations configs = new Configurations();
            File configFile = new File(PropertiesReader.class.getClassLoader()
                    .getResource(CONFIG_PROPERTIES).toURI());
            
            Configuration config = configs.properties(configFile);
            LOGGER.info("Configuration loaded successfully from: {}", configFile.getAbsolutePath());
            return config;
            
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_TEXT + e.getMessage(), e);
            throw new RuntimeException(EXCEPTION_TEXT + e.getMessage(), e);
        }
    }
}
