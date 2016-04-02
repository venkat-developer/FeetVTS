package com.i10n.tools.skins.config;

/*
 * 
 * The Interface that Should be implemented by all the classes that provides configuration
 * information 
 * 
 * Configuration Information : see {@link Configuration}
 * 
 * @Autor :- N.Balaji
 */
interface IConfigurationManager {
    /**
     * Gets The Configuration Object
     */
    public Configuration getConfig() throws Exception;
}
