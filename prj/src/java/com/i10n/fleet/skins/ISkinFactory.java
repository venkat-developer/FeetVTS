package com.i10n.fleet.skins;

import org.w3c.dom.Document;

import com.i10n.fleet.datasets.impl.Dataset;

/**
 * Interface for Skinfactories
 * 
 * @see SkinFactory
 * @author sabarish
 */
public interface ISkinFactory {
    /**
     * Returns a {@link Dataset} from the skin config file(contains the list of
     * skins) specified.
     * 
     * @param doc
     * @return
     */
    Dataset getSkins(Document doc);
}
