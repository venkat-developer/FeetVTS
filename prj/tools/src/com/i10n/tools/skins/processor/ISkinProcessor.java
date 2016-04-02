package com.i10n.tools.skins.processor;

import org.w3c.dom.Document;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.exception.SkinParseException;

/**
 * Interface for the processors on {@link Skin}, ex : Merging the CSS and JS
 * Files
 * 
 * @author sabarish
 * 
 */
public interface ISkinProcessor {
    /**
     * Function to start the processing the {@link Document}
     * 
     * @param skin
     */
    Document process(Document input) throws SkinParseException;
}
