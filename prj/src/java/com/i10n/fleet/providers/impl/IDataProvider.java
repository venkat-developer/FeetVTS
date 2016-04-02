package com.i10n.fleet.providers.impl;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Interface for DataProviders that provides data needed for the controller to
 * feed into the freemarker data model
 * 
 * @author sabarish
 * 
 */
public interface IDataProvider {
    /**
     * Returns the name of the DataProvider
     * 
     * @return
     */
    String getName();

    /**
     * Returns an {@link IDataset} representation of the data, based on the
     * {@link RequestParameters}
     * 
     * @return
     */
    IDataset getDataset(RequestParameters params);
}
