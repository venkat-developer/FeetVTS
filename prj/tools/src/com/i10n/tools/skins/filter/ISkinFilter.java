package com.i10n.tools.skins.filter;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.exception.SkinParseException;

/**
 * Interface for various skin filters.
 * 
 * @see com.i10n.tools.skins.filter.impl.SkinCompressor
 * @see com.i10n.tools.skins.filter.impl.SkinMerger
 * @author sabarish
 * 
 */
public interface ISkinFilter {
    /**
     * Filters the skin
     * 
     * @param skin
     * @return
     * @throws SkinParseException
     */
    Skin filter(Skin skin) throws SkinParseException;
}
