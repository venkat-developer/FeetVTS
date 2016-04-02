package com.i10n.fleet.datasets.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.INamedDataset;

/**
 * Implementation of {@link IDataset}
 * 
 * @author sabarish
 * 
 */
public class Dataset extends LinkedHashMap<String, Object> implements IDataset {

	private static final long serialVersionUID = -2727386090214000286L;
	
//  private static final Logger LOG = Logger.getLogger(Dataset.class);

    public Dataset() {
        super();
    }

    // inherited javadoc
    public Dataset(Map<? extends String, ? extends Object> m) {
        super(m);
        /* TODO : Add Logic for converting deep map to IDataset */
    }

    /**
     * Adds a {@link NamedDataset} with the dataset's name as the key
     * 
     * @param dataset
     */
    public void addNamedDataset(INamedDataset dataset) {
        put(dataset.getName(), dataset);
    }

    /**
     * Adds all in the list of {@link NamedDataset} to the current dataset
     * 
     * @param datasets
     */
    public void addAllNamedDatasets(List<? extends INamedDataset> datasets) {
        for (INamedDataset dataset : datasets) {
            addNamedDataset(dataset);
        }
    }

    /**
     * See {@link Map#containsKey(Object)}
     */
    @Override
    public boolean containsKey(Object object) {
        boolean result = false;
        String path = (String) object;
        StringTokenizer pathTokenizer = new StringTokenizer(path, DELIMITER);
        Object obj = this;
        while (null != obj && obj instanceof IDataset) {
            String token = pathTokenizer.nextToken();

            IDataset parent = (IDataset) obj;
            obj = parent.get(token);

            if (!pathTokenizer.hasMoreTokens()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * See {@link Map#put(Object, Object))}
     */
    @Override
    public Object put(String path, Object value) {
        Object result = null;

        if (null != path && path.contains(DELIMITER)) {
            // First, find the path at which the element should exist
            IDataset parent = null;
            String token = null;

            StringTokenizer pathTokenizer = new StringTokenizer(path, DELIMITER);
            Object obj = this;
            while (null != obj && obj instanceof IDataset) {
                token = pathTokenizer.nextToken();

                parent = (IDataset) obj;
                obj = parent.get(token);

                if (!pathTokenizer.hasMoreTokens()) {
                    // Full path found and is populated
                    result = parent.put(token, value);
                    break;
                }
            }

            // If the full path did not yet exist in the dataset...
            if (null == result) {
                // fill missed out tokens...
                while (pathTokenizer.hasMoreTokens()) {
                    IDataset d = new Dataset();
                    parent.put(token, d);
                    parent = d;
                    token = pathTokenizer.nextToken();
                }
                // place the explicit value at the end location.
                parent.put(token, value);
            }
        }
        else {
            result = super.put(path, value);
        }
        return result;
    }

    /**
     * See {@link Map#get(Object)}
     */
    public Object get(String path) {
        Object result = null;

        if (path.contains(DELIMITER)) {
            Object obj = this;
            final StringTokenizer pathTokenizer = new StringTokenizer(path, DELIMITER);
            IDataset map = this;
            while (null != obj && obj instanceof IDataset) {
                final String token = pathTokenizer.nextToken();
                map = (IDataset) obj;
                obj = map.get(token);
                if (!pathTokenizer.hasMoreTokens()) {
                    result = obj;
                    break;
                }
            }
        }
        else {
            result = super.get(path);
        }
        return result;
    }

    /**
     * See {@link IDataset#getDataset(String)}
     */
    public IDataset getDataset(String path) {
        return (IDataset) this.get(path);
    }

    /**
     * See {@link IDataset#getValue(String)}
     */
    public String getValue(String path) {
        Object obj = get(path);
        return (String) obj;
    }

    /**
     * See {@link IDataset#getBoolean(String)}
     */
    public boolean getBoolean(String path) {
        boolean result = false;
        Object obj = get(path);
        if (null != obj && obj instanceof Boolean) {
            result = ((Boolean) obj).booleanValue();
        }
        return result;
    }

    /**
     * See {@link IDataset#copy(IDataset)}
     */
    public IDataset copy() {
        IDataset result = new Dataset();
        for (Entry<String, Object> entry : this.entrySet()) {
            if (entry.getValue() instanceof IDataset) {
                result.put(entry.getKey(), ((IDataset) entry.getValue()).copy());
            }
            else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}