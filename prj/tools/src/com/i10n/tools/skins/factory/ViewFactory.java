package com.i10n.tools.skins.factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.beans.View;
import com.i10n.tools.utils.XPathUtils;

/**
 * A Factory for generating {@link View} for the {@link Skin} : skin defined
 * inside a given {@link Node} : node.
 * 
 * @author sabarish
 * 
 */
public class ViewFactory extends FileFactory {

    private LibraryFactory m_libraryFactory = new LibraryFactory();
    private FileFactory m_fileFactory = new FileFactory();

    /**
     * Returns {@link Map} of {@link View} for the given {@link Skin} in the
     * given {@link Node}
     * 
     * @param skin
     * @param node
     * @return
     */
    public Map<String, View> getViews(Skin skin, Node node) {

        Map<String, View> views = new LinkedHashMap<String, View>();
        List<Node> viewNodes = XPathUtils.getNodes(node, "view");
        for (Node viewNode : viewNodes) {
            View view = new View();
            String name = XPathUtils.getAttribute(viewNode, "name");
            view.setName(name);
            Node libraries = XPathUtils.getNode(viewNode, "libraries");
            view.addLibraries(m_libraryFactory.getLibraries(skin, libraries));
            view.addAllFiles(m_fileFactory.getFiles(skin.getNamespace(), viewNode));
            views.put(name, view);
        }
        return views;
    }
}
