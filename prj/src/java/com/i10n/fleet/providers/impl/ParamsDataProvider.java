package com.i10n.fleet.providers.impl;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * A Data providers for giving information on Parameters passed
 * 
 * @author sabarish
 * 
 */
public class ParamsDataProvider implements IDataProvider {

    /**
     * Returns the {@link IDataset} of parameters passed to the application
     */
    public IDataset getDataset(RequestParameters params) {
        IDataset dataset = new Dataset();
        for (RequestParams param : RequestParams.values()) {
            String value = params.getParameter(param);
            if (null != value) {
                dataset.put(param.toString(), value);
            }
        }
        return dataset;
    }

    /**
     * See {@link IDataProvider#getName()}
     */
    public String getName() {
        return "parameters";
    }

}
