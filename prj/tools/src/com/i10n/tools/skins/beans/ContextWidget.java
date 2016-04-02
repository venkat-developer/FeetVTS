package com.i10n.tools.skins.beans;

import com.i10n.tools.skins.files.FTLFile;
import com.i10n.tools.skins.files.JSFile;

/**
 * Represents a Context Widget, i.e. widgets that dont have a UI , but rather
 * just is a background widget that provides as a data provider or manager for
 * other UI Widgets ({@link UIWidget}). Such widgets wont have a {@link FTLFile}
 * in its fileset, but will have atleast one {@link JSFile} added.
 * 
 * @see Widget
 * @see UIWidget
 * @see View
 * @author sabarish
 */
public class ContextWidget extends Widget {
    /**
     * See {@link IWidget#getType()}
     */
    public WidgetType getType() {
        return WidgetType.CONTEXT;
    }
}
