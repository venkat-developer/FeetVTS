package com.i10n.tools.skins.processor.utils;

import java.util.List;

import org.w3c.dom.Node;

import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.utils.XPathUtils;

/**
 * 
 * The class has the following responsibilities
 * 
 * 1.Removes the Items inherited from the base class that are overridden in the
 * subclass
 * 
 * 2. Handles the removal of nodes
 * 
 * @author N.Balaji
 * 
 */
public class InheritanceHandler {
    /*
     * The following are the list of elements that can specify removals
     */
    private final String[] m_removalsList;

    public InheritanceHandler() {
        SkinProperties.load();
        m_removalsList = SkinProperties.getProperty(SkinProperties.REMOVALS_LIST).split(
                ",");
    }

    /*
     * method that implements overriding of a base node by the child nodes
     */
    public void implementOverride(List<Node> input, String nameOfElementWithClosestScope) {
        /*
         * CAUTION: THE input ELEMENT SHOULD NOT BE STRUCTURALLY MODIFIED BY
         * METHODS LIKE remove(). THIS MIGHT LEAD TO UNPREDICTABLE CONDITIONS
         */
        for (int i = 0; i < input.size(); i++) {
            Node node = input.get(i);
            String nameAttr = XPathUtils.getAttribute(node, "name");
            if (nameAttr != null) {
                if (nameAttr.equalsIgnoreCase(nameOfElementWithClosestScope)) {
                    if (node.getParentNode() != null) {
                        /* Is the Node already Removed from the tree */
                        node.getParentNode().removeChild(node);
                    }
                }
            }
        }
    }

    public void implementRemovals(Node root, String nameOfTheCurrentSkin) {
        /*
         * CAUTION: THE java.Util ELEMENT SHOULD NOT BE STRUCTURALLY MODIFIED BY
         * METHODS LIKE remove(). THIS MIGHT LEAD TO UNPREDICTABLE CONDITIONS
         */
        for (int i = 0; i < m_removalsList.length; i++) {
            List<Node> targetList = XPathUtils.getNodes(root, m_removalsList[i]);
            for (int j = 0; j < targetList.size(); j++) {
                Node curParentNode = targetList.get(j);
                /* Select the filters that match this element */
                List<Node> filterList = XPathUtils.getNodes(targetList.get(j),
                        "filterset/filter");
                for (int k = 0; k < filterList.size(); k++) {
                    Node curFilter = filterList.get(k);
                    String element = XPathUtils.getAttribute(curFilter, "element");
                    String removeBy = XPathUtils.getAttribute(curFilter, "removeby");
                    String key = XPathUtils.getAttribute(curFilter, "key");
                    if (removeBy.equalsIgnoreCase("content"))
                        removeByContent(curParentNode, element, key);
                    else if (removeBy.equalsIgnoreCase("attribute")) {

                        String[] keyValuePair = key.split(":");
                        if (keyValuePair.length == 2) {
                            removeByAttribute(curParentNode, element, keyValuePair[0],
                                    keyValuePair[1]);
                        }

                    }
                }
            }
        }
    }

    private void removeByAttribute(Node parent, String element, String attrName,
            String value) {
        List<Node> listOfMatchingChildren = XPathUtils.getNodes(parent, element);
        for (int i = 0; i < listOfMatchingChildren.size(); i++) {
            Node curNode = listOfMatchingChildren.get(i);
            /* Is the node present the tree */
            if (curNode.getParentNode() != null) {
                String attrValue = XPathUtils.getAttribute(curNode, attrName);
                if (attrValue != null) {
                    if (attrValue.equalsIgnoreCase(value)) parent.removeChild(curNode);
                }
            }
        }
    }

    private void removeByContent(Node parent, String element, String key) {
        List<Node> listOfMatchingChildren = XPathUtils.getNodes(parent, element);
        for (int i = 0; i < listOfMatchingChildren.size(); i++) {
            Node curNode = listOfMatchingChildren.get(i);
            /* Is the node present the tree */
            if (curNode.getParentNode() != null) {
                String content = curNode.getTextContent().trim();
                if (content != null) {
                    if (content.indexOf(key) >= 0) parent.removeChild(curNode);
                }
            }
        }
    }

}
