package com.i10n.fleet.providers.mock;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Mock : Mock Data Provider for System Settings. This class will be removed in
 * future.
 * 
 * @author Sabarish
 * 
 */
public class SystemSettingsDataProvider implements IDataProvider {

    private static enum FETCH_TYPE {
        STREAMING, POLLING
    };

    private static final int POLLING_INTERVAL = 30;

    /**
     * @see IDataProvider#getDataset(RequestParameters)
     */
    public IDataset getDataset(RequestParameters params) {
        IDataset data = new Dataset();
        data.put("fetchmethod", FETCH_TYPE.STREAMING.toString());
        data.put("polling.interval", POLLING_INTERVAL);
        return data;
    }

    /**
     * @see IDataProvider#getName()
     */
    public String getName() {
        return "systemsettings";
    }

}
