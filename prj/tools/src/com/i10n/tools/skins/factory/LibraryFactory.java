package com.i10n.tools.skins.factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.i10n.tools.skins.beans.ILibrary;
import com.i10n.tools.skins.beans.Library;
import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.utils.XPathUtils;

/**
 * Factory for generating {@library Library} based on the {@link Node} : node
 * and {@link Skin} : skin passed.
 * 
 * @author sabarish
 * 
 */
public class LibraryFactory {

    private FileFactory m_fileFactory = new FileFactory();

    /**
     * Returns the list of {@link Library} defined inside {@link Node} : node
     * for the given {@link Skin} : skin.
     * 
     * @param skin
     * @param root
     * @return
     */
    public Map<String, ILibrary> getLibraries(Skin skin, Node root) {
        Map<String, ILibrary> libraries = new LinkedHashMap<String, ILibrary>();
        List<Node> libraryNodes = XPathUtils.getNodes(root, "library");
        for (Node libraryNode : libraryNodes) {
            Library library = getLibrary(skin, libraryNode);
            libraries.put(library.getName(), library);
        }
        return libraries;
    }

    private Library getLibrary(Skin skin, Node node) {
        Library result = null;
        String name = XPathUtils.getAttribute(node, "name");
        if (null != name && !name.isEmpty()) {
            result = new Library(name);
            result.addAllFiles(m_fileFactory.getFiles(skin.getNamespace(), node));
        }
        return result;
    }
}
