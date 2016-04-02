package com.i10n.fleet.providers.mock.managers;

import com.i10n.fleet.datasets.IDataset;

/**
 * Mock : An interface for all Data Managers.This interface will be removed in
 * future
 * 
 * @author Sabarish
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
