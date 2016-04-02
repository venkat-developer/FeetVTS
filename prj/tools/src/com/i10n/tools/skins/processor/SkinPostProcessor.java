package com.i10n.tools.skins.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.skins.processor.utils.SkinElementClassifier;
import com.i10n.tools.utils.XMLUtils;
import com.i10n.tools.utils.XPathUtils;

/**
 * Class for optimizing the Blotted XML File
 * 
 * @author N.Balaji
 */
public class SkinPostProcessor implements ISkinProcessor {

    private String[] m_scriptXPath = {
        "./scripts/script"
    };
    private String[] m_styleXPath = {
        "./stylesheets/stylesheet",
    };
    private String[] m_templateXPath = {
        "./template"
    };

    private String[] m_dataXPath = {
        "./data"
    };

    /*
     * The following enum consists of elements that are duplicated.
     * 
     * Each enumerated entity corresponds to a duplicated element in the
     * skin.xml
     * 
     * The behaviour of such an entity is to return the paths where they are
     * duplicated.
     * 
     * This member is used by the handleDuplicatedElements method
     */
    private static enum DUPLICATED_ELEMENT {
        script {
            String[] paths = {
                "./library//script"
            };

            @Override
            public String[] getPaths() {
                return paths;
            }
        },
        stylesheet {
            String[] paths = {
                "./library//stylesheet"
            };

            @Override
            public String[] getPaths() {
                return paths;
            }
        };
        abstract String[] getPaths();
    }

    private List<String> m_dependencyList;

    /**
     * Post Processes the {@link Document} : input and removes all the
     * redundant/non existing files
     */
    public Document process(Document input) throws SkinParseException {
        Document result = input;
        String skinName = XPathUtils.getAttribute(XPathUtils.getNode(input, "/skin"),
                "name");

        List<Node> views = XPathUtils.getNodes(result.getDocumentElement(), "//view");
        List<Node> widgets = XPathUtils.getNodes(result.getDocumentElement(), "//widget");
        List<Node> emptyNodes = null;
        /* Handling the views */
        for (Node view : views) {
            for (String xpath : m_scriptXPath) {
                List<Node> nodeList = XPathUtils.getNodes(view, xpath);
                processNodes(nodeList, skinName, "script");
            }
            for (String xpath : m_styleXPath) {
                List<Node> nodeList = XPathUtils.getNodes(view, xpath);
                processNodes(nodeList, skinName, "stylesheet");
            }
            for (String xpath : m_templateXPath) {
                List<Node> nodeList = XPathUtils.getNodes(view, xpath);
                processNodes(nodeList, skinName, "template");
            }
            for (String xpath : m_dataXPath) {
                List<Node> nodeList = XPathUtils.getNodes(view, xpath);
                processNodes(nodeList, skinName, "data");
            }
            /* Special handling for libraries */
            handleDuplicatedElements(XPathUtils.getNodes(view, "./libraries"), skinName);
        }
        /* Handling the Widgets */
        for (Node widget : widgets) {
            for (String xpath : m_scriptXPath) {
                List<Node> nodeList = XPathUtils.getNodes(widget, xpath);
                processNodes(nodeList, skinName, "script");
            }
            for (String xpath : m_styleXPath) {
                List<Node> nodeList = XPathUtils.getNodes(widget, xpath);
                processNodes(nodeList, skinName, "stylesheet");
            }
            for (String xpath : m_templateXPath) {
                List<Node> nodeList = XPathUtils.getNodes(widget, xpath);
                processNodes(nodeList, skinName, "template");
            }
            for (String xpath : m_dataXPath) {
                List<Node> nodeList = XPathUtils.getNodes(widget, xpath);
                processNodes(nodeList, skinName, "data");
            }
        }
        /* Removing any Empty nodes Generated Here */
        do {
            emptyNodes = XPathUtils.getNodes(result.getDocumentElement(),
                    "//*[not(node()) and not(text())]");
            XMLUtils.removeElements(emptyNodes);
        }
        while (!emptyNodes.isEmpty());
        return result;
    }

    private void processNodes(List<Node> nodeList, String skinName, String elementType) {
        /* Instantiating necessary classes */
        SkinElementClassifier classifier = new SkinElementClassifier();

        if (classifier.classify(elementType).contains(SkinElementClassifier.HYBRID_TYPE)) {
            /* Handling elements that can have links to external entities */
            processElements(nodeList, skinName, elementType, true);
        }
        else if (classifier.classify(elementType).contains(
                SkinElementClassifier.INTERNAL_TYPE)) {
            /* Handling elements that do not have links to external entities */
            processElements(nodeList, skinName, elementType, false);
        }
    }

    private void processElements(List<Node> nodeList, String skinName,
            String elementType, boolean hybrid) {
        /* Instantiating necessary classes */
        SkinElementClassifier classifier = new SkinElementClassifier();

        /* Taking a copy of the dependency lists */
        List<String> skinPaths = new ArrayList<String>();
        skinPaths.addAll(m_dependencyList);
        /* ordering the elements */
        if (classifier.classify(elementType).contains(
                SkinElementClassifier.BASEELEMENTSFIRST_TYPE))
            skinPaths = reverseListOrder(skinPaths);
        for (Node curNode : nodeList) {
            String nodeText = curNode.getTextContent().trim();
            nodeText = nodeText.replaceAll("\n", "");
            /* Check whether the element has an external link */
            if (hybrid && nodeText.contains("http")) {
                String finalText = nodeText.substring(nodeText.indexOf("http"));
                curNode.setTextContent(finalText);
                continue;
            }
            /* if not external check for libraries */
            else if (nodeText.contains("@STATIC_DIR@")) {
                /*
                 * Only library elements have @STATIC_DIR@ added to them by the
                 * XSLTs
                 */
                File tempFile = new File(getLibraryPath(nodeText.substring("@STATIC_DIR@"
                        .length())));
                if (!tempFile.exists()) {
                    removeChild(curNode.getParentNode(), curNode);
                }
                continue;
            }
            else {
                /* Check whether the file is present in any of the skin paths */
                int count = 0;
                boolean found = false;
                for (String skinPath : skinPaths) {
                    File tempFile = new File(getFilePath(nodeText, skinPath));
                    if (tempFile.exists()) {
                        found = true;
                        if (classifier.classify(elementType).contains(
                                SkinElementClassifier.OVERRIDEN_TYPE)) {
                            curNode.setTextContent(getFinalFilePath(nodeText, skinPath,
                                    elementType));
                            break;
                        }
                        else {
                            /*
                             * The first presence will have a node in the node
                             * tree. for all others we have to manually add
                             */
                            if (count > 0) {
                                Node sibling = curNode.cloneNode(false);
                                sibling.setTextContent(getFinalFilePath(nodeText,
                                        skinPath, elementType));
                                appendChild(curNode.getParentNode(), sibling);
                            }
                            else {
                                curNode.setTextContent(getFinalFilePath(nodeText,
                                        skinPath, elementType));
                            }
                        }
                        count++;
                    }
                }
                if (!found) removeChild(curNode.getParentNode(), curNode);
            }
        }
    }

    private void removeChild(Node parent, Node child) {
        if (parent != null) parent.removeChild(child);
    }

    private void appendChild(Node parent, Node child) {
        if (parent != null) {
            parent.appendChild(child);
        }
    }

    private String getLibraryPath(String nodeText) {
        String path = SkinProperties.getProperty(SkinProperties.STATIC_DIR_KEY);
        if (nodeText.startsWith("/"))
            return path + nodeText;
        else
            return path + "/" + nodeText;
    }

    private String getFilePath(String nodeText, String skinName) {
        String path = SkinProperties.getProperty(SkinProperties.STATIC_DIR_KEY);
        if (nodeText.startsWith("/"))
            return path + "/skins/" + skinName + nodeText;
        else
            return path + "/skins/" + skinName + "/" + nodeText;
    }

    private String getFinalFilePath(String nodeText, String skinName, String elementType) {
        /* Instantiating necessary classes */
        SkinElementClassifier classifier = new SkinElementClassifier();
        String staticKey = "";
        if (classifier.classify(elementType).contains(SkinElementClassifier.STATIC_TYPE))
            staticKey = "@STATIC_DIR@";
        if (nodeText.startsWith("/"))
            return staticKey + "/skins/" + skinName + nodeText;
        else
            return staticKey + "/skins/" + skinName + "/" + nodeText;
    }

    private List<String> reverseListOrder(List<String> srcList) {

        List<String> result = new ArrayList<String>();
        for (int i = srcList.size() - 1; i >= 0; i--)
            result.add(srcList.get(i));

        return result;
    }

    public List<String> getDependencyList() {
        return m_dependencyList;
    }

    public void setDependencyList(List<String> dependencyList) {
        this.m_dependencyList = dependencyList;
    }

    private void handleDuplicatedElements(List<Node> parents, String skinName) {
        for (Node parent : parents) {
            for (DUPLICATED_ELEMENT duplicatedElement : DUPLICATED_ELEMENT.values()) {
                String element = duplicatedElement.name();
                String[] duplicatedPaths = duplicatedElement.getPaths();
                for (String duplicatedPath : duplicatedPaths) {
                    /*
                     * Do the initial processing of checking if the file exists
                     * etc
                     */
                    List<Node> duplicatedElements = XPathUtils.getNodes(parent,
                            duplicatedPath);
                    processNodes(duplicatedElements, skinName, element);
                    /* Removing the duplicated Elements */
                    for (int j = 0; j < duplicatedElements.size(); j++) {
                        Node curNode = duplicatedElements.get(j);
                        String text = curNode.getTextContent().trim();
                        for (int k = j + 1; k < duplicatedElements.size(); k++) {
                            Node sibling = duplicatedElements.get(k);
                            String sibText = sibling.getTextContent().trim();
                            if (text.equals(sibText)) {
                                removeChild(sibling.getParentNode(), sibling);
                            }
                        }
                    }
                }
            }
        }
    }
}
