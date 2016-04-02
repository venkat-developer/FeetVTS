package com.i10n.fleet.providers.managers;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.datasets.impl.SkinSetDataset;

/**
 * Interface for managers that generates a {@link Dataset} that represents
 * the informations for each skin after parsing the skin xml's
 * 
 * @author sabarish
 * 
 */
public interface ISkinManager extends IManager<SkinSetDataset> {
    /**
     * Returns a generated {@link SkinDataset}
     */
    Dataset getSkin();
}
