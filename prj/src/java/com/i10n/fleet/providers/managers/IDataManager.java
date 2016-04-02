package com.i10n.fleet.providers.managers;

import com.i10n.fleet.datasets.IDataset;

/**
 * 
 * 
 * @author Antony
 * 
 */
public interface IDataManager {
    /**
     * Returns the data and filters it based on the options passed.
     * 
     * @param options
     * @return
     */
    IDataset getData(IDataset options);
}
