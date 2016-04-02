package com.i10n.tools.skins.beans;

/**
 * Implements {@link IWidget} and is an abstract implementation for
 * {@link UIWidget} , {@link ContextWidget} and {@link View}
 * 
 * @see UIWidget
 * @see ContextWidget
 * @see View
 * @author sabarish
 * 
 */
public abstract class Widget extends AbstractFileSetComponent implements IWidget {
    private String m_name = "";

    /**
     * See {@link IWidget#getName()}
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name of the widget
     * 
     * @param m_name
     */
    public void setName(String name) {
        m_name = name;
    }

}
