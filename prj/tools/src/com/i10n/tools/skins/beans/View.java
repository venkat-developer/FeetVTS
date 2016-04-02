package com.i10n.tools.skins.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a page in the UI. This is combination of widgets({@link Widget}).
 * 
 * @see Widget
 * @see UIWidget
 * @see View
 * @author sabarish
 * 
 */
public class View extends Widget implements IView {

    private Map<String, ILibrary> m_libraries = new LinkedHashMap<String, ILibrary>();

    /**
     * See {@link IWidget#getType()}
     */
    public WidgetType getType() {
        return WidgetType.VIEW;
    }

    /**
     * Returns a {@link List} of {@link ILibrary} added in the View
     * 
     * @return
     */
    public List<ILibrary> getLibraries() {
        return new ArrayList<ILibrary>(m_libraries.values());
    }

    /**
     * Returns the {@link ILibrary} with the name given added in the view
     * 
     * @param name
     * @return
     */
    public ILibrary getLibrary(String name) {
        return m_libraries.get(name);
    }

    /**
     * Adds all the libraries in the map to current map of libraries
     * 
     * @param libraries
     */
    public void addLibraries(Map<String, ILibrary> libraries) {
        m_libraries.putAll(libraries);
    }

}
