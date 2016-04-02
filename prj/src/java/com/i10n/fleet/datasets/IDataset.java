package com.i10n.fleet.datasets;

import java.util.Map;

/**
 * data for the ui(freemarker data model) , is represented as {@link IDataset},
 * which uses nested maps and allows to retrieve the data deep into the map.
 * 
 * @author sabarish
 * 
 */
public interface IDataset extends Map<String, Object> {
    /**
     * @param path
     *            Delimited path to the target object
     * 
     * @return The object residing at the specified path
     * 
     * @see #DELIMITER
     */
    Object get(String path);

    /**
     * Returns a (possibly deeply nested) value from this map.
     * 
     * @param path
     *            Delimited path to the target value
     * @return
     * 
     * @see #DELIMITER
     */
    String getValue(String path);

    /**
     * Returns a (possibly deeply nested) boolean value from this map.
     * 
     * @param path
     *            Delimited path to the target value
     * @return
     * 
     * @see #DELIMITER
     */
    boolean getBoolean(String path);

    /**
     * @param path
     *            Delimited path to the target descendant map
     * @return
     * 
     * @see #DELIMITER
     */
    IDataset getDataset(String path);

    /**
     * Returns a copy of the current dataset
     * 
     * @return
     */
    IDataset copy();

    /**
     * The delimiter to be used in deeply-nested requests. Currently set to .,
     * this may be changed in the future if keys in the dataset begin to use
     * that character as a valid idchar.
     */
    String DELIMITER = ".";
}
