package com.i10n.fleet.providers.mock.managers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.XPathUtils;

/**
 * Mock : Provides default functionalities for {@link IDataManager} which
 * delegate certain responsibilities to other data managers. This class will be
 * removed in future
 * 
 * @author Sabarish
 * 
 */
public abstract class AbstractDelegatedDataManager extends AbstractDataManager {

    private Map<String, IDataManager> m_dataManagers;

    /**
     * Overrides {@link AbstractDataManager#getNodeData(Node, IDataset)} to
     * delegate responsibilities to the delegated Managers
     */
    @Override
    protected IDataset getNodeData(Node root, IDataset options) {
        IDataset result = new Dataset();
        List<Node> nodes = XPathUtils.getNodes(root, "*");
        for (Node node : nodes) {
            String name = node.getNodeName();
            String value = node.getTextContent();
            if (!isSkippable(name, options) && null != value) {
                value = value.trim();
                if (null != m_dataManagers && m_dataManagers.get(name) != null) {
                    IDataManager manager = m_dataManagers.get(name);
                    IDataset managerData = manager.getData(getDelegatorOptions(name,
                            "id", value));
                    if (null != managerData.getDataset(value)) {
                        for (Entry<String, Object> entry : managerData.getDataset(value)
                                .entrySet()) {
                            if (!isSkippable(name + entry.getKey(), options)) {
                                result.put(name + entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
                else {
                    result.put(name, value);
                }
            }
        }
        return result;
    }

    /**
     * Returns the options needed for delegator managers. Requires all the child
     * class to override {@link #getDataID()}
     * 
     * @param delegator
     * @param filterID
     * @param filterValue
     * @return
     */
    protected IDataset getDelegatorOptions(String delegator, String filterID,
            String filterValue) {
        IDataset result = new Dataset();
        result.put("filter." + filterID, filterValue);
        if (null != getDataID()) {
            result.put("skip." + getDataID(), true);
        }
        return result;
    }

    /**
     * Returns the {@link Map} of delegator {@link IDataManager}
     * 
     * @return
     */
    public Map<String, IDataManager> getDataManagers() {
        return m_dataManagers;
    }

    /**
     * Sets the {@link Map} of delegator {@link IDataManager}
     * 
     * @param dataManagers
     */
    public void setDataManagers(Map<String, IDataManager> dataManagers) {
        m_dataManagers = dataManagers;
    }

    /**
     * Returns dataid needed to check for loops while delagating manager All the
     * child classes must override this.
     * 
     * @return
     */
    protected abstract String getDataID();

}
