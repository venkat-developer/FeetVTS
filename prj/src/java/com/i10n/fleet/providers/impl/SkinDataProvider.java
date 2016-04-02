package com.i10n.fleet.providers.impl;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.providers.managers.ISkinManager;
import com.i10n.fleet.providers.managers.impl.SkinManager;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Skin Dataset Provider : provides with data specific to skins.
 * 
 * @author sabarish
 * 
 */
public class SkinDataProvider implements IDataProvider {

    private ISkinManager m_skinManager;

    private static final String PROVIDER_NAME = "skins";

    /**
     * See {@link IDataProvider#getDataset(RequestParameters)}
     */
    public IDataset getDataset(RequestParameters params) {
        IDataset dataset = m_skinManager.getSkin();
        return dataset;
    }

    /**
     * See {@link IDataProvider#getName()}
     */
    public String getName() {
        return PROVIDER_NAME;
    }

    /**
     * Gets the {@link SkinManager} : skinManager
     * 
     * @return
     */
    public ISkinManager getSkinManager() {
        return m_skinManager;
    }

    /**
     * Sets the {@link SkinManager} : skinManager
     * 
     * @param skinManager
     */
    public void setSkinManager(ISkinManager skinManager) {
        m_skinManager = skinManager;
    }

}
