package com.example.config;

import static com.google.common.base.Preconditions.*;

import com.example.config.constants.ApplicationProperties;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Ths object helps us to simulate a container of values to be injected. Non - testable code. 
 * 
 * @author Swarn Avinash Kumar
 */
@Deprecated
@ThreadSafe
public final class Environment {

    private static Environment instance;

    private Properties properties;

    /**
     * Private constructor ( Singleton pattern)
     */
    private Environment() {

    }

    /**
     * Get the instance of Environment (Singleton pattern). 
     *
     * @return single instance of Environment
     * @author Swarn Avinash Kumar
     */
    public static Environment getInstance() {
        if (instance == null) {
            synchronized (Environment.class) {
                if (instance == null) {
                    instance = new Environment();
                    instance.loadProperties();
                }
            }
        }
        return instance;
    }

    /**
     * Load properties.
     */
    private void loadProperties() {
        properties = new Properties();
        final URL url = Resources.getResource(ApplicationProperties.APPLICATION_PROPERTIES_FILENAME);
        final ByteSource byteSource = Resources.asByteSource(url);
        InputStream inputStream = null;
        try {
            inputStream = byteSource.openBufferedStream();
            properties.load(inputStream);
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the property value from a given key.
     *
     * @param propertyKey the property key
     * @return the property value
     * @author Swarn Avinash Kumar
     */
    @Deprecated
    public String getPropertyValue(final String propertyKey) {
        checkState(properties != null, "Properties are not loaded");
        checkArgument(propertyKey != null);
        return properties.getProperty(propertyKey);
    }
}