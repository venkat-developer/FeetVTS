package com.i10n.tools.skins.beans;

import com.i10n.tools.skins.files.FTLFile;

/**
 * Represents a UI Widget. Such widgets will have a single {@link FTLFile} added
 * in it's fileset
 * 
 * @see Widget
 * @see UIWidget
 * @see View
 * @author sabarish
 * 
 */
public class UIWidget extends Widget {

    public WidgetType getType() {
        return WidgetType.UI;
    }
}
