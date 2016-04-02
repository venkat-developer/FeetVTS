package com.i10n.fleet.datasets;

/**
 * Represents a {@link IDataset} with name. helps in directly adding to datasets
 * with it's name as key
 * 
 * @author sabarish
 * 
 */
public interface INamedDataset extends IDataset {
    /**
     * Returns the name of the NamedDataset
     * 
     * @return
     */
    String getName();
}
