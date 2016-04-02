package com.i10n.tools.skins.beans;

/**
 * Implementation of {@link ILibrary}
 * 
 * @author sabarish
 * 
 */
public class Library extends AbstractFileSetComponent implements ILibrary {
    private String m_name;

    public Library(String name) {
        m_name = name;
    }

    /**
     * See {@link ILibrary#getName()}
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name of the library
     * 
     * @param name
     */
    public void setName(String name) {
        m_name = name;
    }

}
