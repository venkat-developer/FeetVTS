package com.i10n.fleet.datasets.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension to {@link Dataset} for providing skin functionalities
 * 
 * @author sabarish
 * 
 */
public class SkinSetDataset extends NamedDataset {

    /**
     * 
     */
    private static final long serialVersionUID = -2614956734401804094L;

    /**
     * Checks whether a skin exists or not
     * 
     * @param name
     * @return
     */
    public boolean hasSkin(String name) {
        return containsKey(name);
    }

    /**
     * returns a list of supported skins.
     * 
     * @return
     */
    public List<String> getSupportedSkins() {
        return new ArrayList<String>(this.keySet());
    }

}
