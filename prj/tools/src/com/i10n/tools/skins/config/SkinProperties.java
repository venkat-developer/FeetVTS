package com.i10n.tools.skins.config;

import org.apache.log4j.Logger;

/**
 * Static classes for storing Skin related properties
 * 
 * @author sabarish
 * 
 */
public final class SkinProperties {
    private SkinProperties() {

    }

    private static Configuration g_config = null;

    private static final Logger LOG = Logger.getLogger(SkinProperties.class);

    public static final String SKIN_DIR_KEY = "SKIN_DIR";
    public static final String STATIC_DIR_KEY = "STATIC_DIR";
    public static final String CYCLIC_CHK_ENABLED_KEY = "CYCLIC_CHK_ENABLED";
    public static final String XSLT_DIR_KEY = "XSLT_DIR";
    public static final String COMPRESS_ENABLED = "STATIC_COMPRESS_ENABLED";
    public static final String MERGE_ENABLED = "STATIC_MERGE_ENABLED";
    public static final String COMPRESS_MUNGE = "COMPRESS_MUNGE";
    public static final String COMPRESS_VERBOSE = "COMPRESS_VERBOSE";
    public static final String COMPRESS_PRESERVESEMICOLONS = "COMPRESS_PRESERVESEMICOLONS";
    public static final String COMPRESS_OPTIMIZE = "COMPRESS_OPTIMIZE";
    public static final String REMOVALS_LIST = "REMOVALS_LIST";

    public static void load() {
        if (null == g_config) {
            try {
                g_config = new PropertyConfigurationManager()
                        .getConfig("skin.properties");
            }
            catch (Exception e) {
                LOG.error("Caught Exception while configuring skin properties");
            }
        }
    }

    public static String getProperty(String key) {
        return (String) g_config.get(key);
    }
}
