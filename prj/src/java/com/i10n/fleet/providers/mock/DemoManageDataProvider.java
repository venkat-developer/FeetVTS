package com.i10n.fleet.providers.mock;

import java.util.Map;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Mock : Mock Data Provider for DemoManager page. This class will be removed
 * in future.
 * 
 * @author Dharmaraju V
 * 
 */
public class DemoManageDataProvider implements IDataProvider {

    private Map<String, IDataProvider> m_delegates = null;

    /**
     * @see IDataProvider#getDataset(RequestParameters)
     */
    public IDataset getDataset(RequestParameters params) {
        String subpage = params.getParameter(RequestParams.subpage);
        IDataset result = new Dataset();
        IDataProvider delegate = m_delegates.get(subpage);
        if (null != delegate) {
            result.put(delegate.getName(), delegate.getDataset(params));
        }
        return result;

    }

    /**
     * @see IDataProvider#getName()
     */
    public String getName() {
        return "demomanage";
    }

    /**
     * Returns the {@link Map} of delegate {@link IDataProvider}
     * 
     * @return
     */
    public Map<String, IDataProvider> getDelegates() {
        return m_delegates;
    }

    /**
     * Sets the {@link Map} of delegate {@link IDataProvider}
     * 
     * @param providers
     */
    public void setDelegates(Map<String, IDataProvider> providers) {
        m_delegates = providers;
    }
}