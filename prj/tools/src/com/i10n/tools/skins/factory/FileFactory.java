package com.i10n.tools.skins.factory;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.UIFile;
import com.i10n.tools.skins.files.IFile.FileType;
import com.i10n.tools.utils.XPathUtils;

public class FileFactory {

    public List<IFile> getFiles(String namespace, Node node) {
        List<IFile> result = new ArrayList<IFile>();
        result.addAll(getCSSFiles(namespace, node));
        result.addAll(getJSFiles(namespace, node));
        IFile file = getFTLFiles(namespace, node);
        if (null != file) {
            result.add(file);
        }
        return result;
    }

    private List<IFile> getCSSFiles(String namespace, Node node) {
        return getFiles(namespace, XPathUtils.getNode(node, "stylesheets"), "stylesheet",
                FileType.CSS);
    }

    private List<IFile> getJSFiles(String namespace, Node node) {
        return getFiles(namespace, XPathUtils.getNode(node, "scripts"), "script",
                FileType.JS);
    }

    private IFile getFTLFiles(String namespace, Node rootNode) {
        IFile file = null;
        Node node = XPathUtils.getNode(rootNode, "template");
        if (null != node) {
            file = getFile(namespace, node, FileType.FTL);
        }
        return file;
    }

    private List<IFile> getFiles(String namespace, Node rootNode, String xpath,
            FileType type) {
        List<IFile> files = new ArrayList<IFile>();
        List<Node> nodes = XPathUtils.getNodes(rootNode, xpath);
        for (Node node : nodes) {
            files.add(getFile(namespace, node, type));
        }
        return files;
    }

    private IFile getFile(String namespace, Node node, FileType type) {
        UIFile file = UIFile.getNewInstanceByType(type);
        String fileName = node.getTextContent();
        file.setFileName(fileName);
        file.setNameSpace(namespace);
        String mergeAttr = XPathUtils.getAttribute(node, "merge");
        String minAttr = XPathUtils.getAttribute(node, "min");
        file.setMergeable(!("false".equals(mergeAttr)));
        file.setCompressible(!("false".equals(minAttr)));
        return file;
    }
}
