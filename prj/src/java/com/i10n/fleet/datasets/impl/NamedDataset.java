package com.i10n.fleet.datasets.impl;

import com.i10n.fleet.datasets.INamedDataset;

/**
 * Implementation of {@link INamedDataset}
 * 
 * @author sabarish
 * 
 */
public class NamedDataset extends Dataset implements INamedDataset {
    /**
     * 
     */
    private static final long serialVersionUID = 1355501152515784713L;
    private String m_name;

    /**
     * See {@link INamedDataset#getName()}
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name of the Dataset
     * 
     * @param name
     */
    public void setName(String name) {
        m_name = name;
    }
}
