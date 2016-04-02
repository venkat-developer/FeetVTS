package com.i10n.fleet.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class PropertyLoader {

    public static final String PROPERTY_FILE = "test.properties";
    private static boolean g_isLoaded = false;

    private static Properties g_properties = new Properties();
    private static Logger LOG = Logger.getLogger(PropertyLoader.class);

    public static void load() {
        if (!g_isLoaded) {
            InputStream propStream = null;
            try {
                propStream = PropertyLoader.class
                        .getResourceAsStream("/" + PROPERTY_FILE);
                g_properties.load(propStream);
            }
            catch (IOException e) {
                LOG.error("Caught IOException while reading test property file", e);
            }
            finally {
                IOUtils.closeQuietly(propStream);
            }
            g_isLoaded = true;
        }
    }

    public static String getProperty(String propName) {
        return g_properties.getProperty(propName);
    }
}
