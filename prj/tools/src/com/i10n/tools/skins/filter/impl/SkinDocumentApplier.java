package com.i10n.tools.skins.filter.impl;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.i10n.tools.skins.beans.ILibrary;
import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.beans.View;
import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.IFileSet;
import com.i10n.tools.skins.files.IFile.FileType;
import com.i10n.tools.utils.XPathUtils;

/**
 * A Helper Class to apply Skin's properties on Document representing the skin
 * 
 * @author sabarish
 * 
 */
public final class SkinDocumentApplier {

    /**
     * A util function to apply Skin'mergedfiles on the Skin Document
     * 
     * @param skin
     * @return
     */
    public static Skin applyMergedFilesOnSkin(Skin skin) {
        Skin result = skin;
        List<View> views = skin.getViews();
        Document skinDoc = skin.getDocument();
        for (View view : views) {
            IFileSet fileset = view.getMergedFileSet();
            String xpath = "/skin/views/view[@name='" + view.getName() + "']";
            Node viewNode = XPathUtils.getNode(skinDoc, xpath);
            addFileSetToNode(skinDoc, viewNode, fileset);
            List<ILibrary> libraries = view.getLibraries();
            for (ILibrary library : libraries) {
                String libXPath = "libraries/library[@name='" + library.getName() + "']";
                Node libNode = XPathUtils.getNode(viewNode, libXPath);
                addFileSetToNode(skin.getDocument(), libNode, library.getMergedFileSet());
            }
        }
        return result;
    }

    private static void addFileSetToNode(Document doc, Node node, IFileSet fileset) {
        Node setContainer = doc.createElement("mergedset");
        Node backLogNode = XPathUtils.getNode(node, "mergedset");
        if (null != backLogNode) {
            backLogNode.getParentNode().removeChild(backLogNode);
        }
        for (FileType fileType : FileType.values()) {
            String pluralNodeName = null;
            String singularNodeName = null;
            if (fileType.equals(FileType.CSS)) {
                pluralNodeName = "stylesheets";
                singularNodeName = "stylesheet";
            }
            else if (fileType.equals(FileType.JS)) {
                pluralNodeName = "scripts";
                singularNodeName = "script";
            }
            if (null != pluralNodeName && singularNodeName != null) {
                List<IFile> files = fileset.getFiles(fileType);
                if (files.size() > 0) {
                    Node container = doc.createElement(pluralNodeName);
                    for (IFile file : files) {
                        Node fileNode = doc.createElement(singularNodeName);
                        Node contentNode = doc.createTextNode(file.getFileName());
                        fileNode.appendChild(contentNode);
                        container.appendChild(fileNode);
                    }
                    setContainer.appendChild(container);
                }
            }
        }
        node.appendChild(setContainer);
    }
}
