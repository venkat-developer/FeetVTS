package com.i10n.fleet.providers.mock;

import java.util.Map.Entry;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.mock.managers.GroupManager;

/**
 * Mock : An abstract class for all the Data Providers that need to group
 * Data.This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public abstract class AbstractGroupedDataProvider {
    private GroupManager m_groupManager;

    /**
     * Groups the data based on group manager.
     * 
     * @param data
     *            data to be be grouped
     * @param key
     *            key in the map to be used to put all the group data
     * @return
     */
    protected IDataset groupDataset(IDataset data, String key) {
        IDataset grpData = m_groupManager.getData(null).copy();
        for (Entry<String, Object> entry : grpData.entrySet()) {
            if (null != entry.getValue() && entry.getValue() instanceof IDataset) {
                ((IDataset) entry.getValue()).put(key, new Dataset());
            }
        }
        for (Object objData : data.values()) {
            if (objData instanceof IDataset) {
                IDataset dataset = (IDataset) objData;
                dataset.remove("groupname");
                if (null != grpData.getDataset(dataset.getValue("groupid") + "." + key)) {
                    IDataset grpDataset = grpData.getDataset(dataset.getValue("groupid")+ "." + key);
                    grpDataset.put(dataset.getValue("id"), dataset);
                }
            }
        }
        return grpData;
    }

    /**
     * Sets the Group Manager
     * 
     * @param manager
     */
    public void setGroupManager(GroupManager manager) {
        m_groupManager = manager;
    }

    /**
     * Returns the Group Manager
     * 
     * @return
     */
    public GroupManager getGroupManager() {
        return m_groupManager;
    }
}