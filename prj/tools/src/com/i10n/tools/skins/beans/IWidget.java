package com.i10n.tools.skins.beans;

import com.i10n.tools.skins.files.IFileSet;

/**
 * Widget Representation.
 * 
 * @author sabarish
 * @see Widget
 * @see UIWidget
 * @see ContextWidget
 * @see View
 */
public interface IWidget {
    /**
     * Returns the name of the widget
     * 
     * @return
     */
    String getName();

    /**
     * Returns the {@link : IFileSet} for the widget
     * 
     * @return
     */
    IFileSet getFileSet();

    /**
     * Returns the type : {@link WidgetType} of the widget
     * 
     * @return
     */
    WidgetType getType();

    /**
     * Enum that Represents the type of Widget. <li>UI Widget : UI</li> <li>
     * Context Widget : CONTEXT</li> <li>View : VIEW</li>
     * 
     * @author sabarish
     * 
     */
    enum WidgetType {
        /**
         * UI Widget Type
         * 
         * @see UIWidget
         */
        UI,
        /**
         * Context Widget Type
         * 
         * @see ContextWidget
         */
        CONTEXT,
        /**
         * View Widget
         * 
         * @see View
         */
        VIEW
    }

}
