package com.i10n.tools.skins.factory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.utils.XPathUtils;

/**
 * A factory class for populating {@link Skin} with values as per the skin xml
 * file.
 * 
 * @author sabarish
 * 
 */
public class SkinFactory {
    private Map<String, Skin> m_skins = new LinkedHashMap<String, Skin>();
    private LibraryFactory m_libraryFactory = new LibraryFactory();
    private ViewFactory m_viewFactory = new ViewFactory();

    /**
     * Populates {@link Skin} object with values as per the skin xml file
     * 
     * @param skin
     * @return
     */
    public Skin manageSkin(Skin skin) {
        m_skins.put(skin.getName(), skin);
        Node libraries = XPathUtils.getNode(skin.getDocument(), "/skin/libraries");
        skin.addLibraries(m_libraryFactory.getLibraries(skin, libraries));
        Node views = XPathUtils.getNode(skin.getDocument(), "/skin/views");
        skin.addViews(m_viewFactory.getViews(skin, views));
        return skin;
    }
}
