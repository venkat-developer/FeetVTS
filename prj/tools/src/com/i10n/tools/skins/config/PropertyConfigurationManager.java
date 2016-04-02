package com.i10n.tools.skins.config;

import java.util.Properties;

/**
 * 
 * A Configuration Manager that reads the Configration Data that is supplied in
 * the form of Java Property Files
 * 
 * By Default looks in the Skin Base directory for the Property File
 * 
 * @author N.Balaji
 */
public class PropertyConfigurationManager implements IConfigurationManager {

    /**
     * 
     * (non-Javadoc)
     * 
     * @see com.i10n.tools.skins.config.IConfigurationManager#getConfig()
     */
    public Configuration getConfig() throws Exception {
        throw new UnsupportedOperationException("Operation not supported yet!");
    }

    public Configuration getConfig(String fileName) throws Exception {

        Configuration skinConfig = new Configuration();
        Properties skinProps = new Properties();
        skinProps.load(getClass().getResourceAsStream("/" + fileName));
        for (Object key : skinProps.keySet()) {
            skinConfig.put((String) key, (String) skinProps.getProperty((String) key));
        }
        return skinConfig;
    }
}
