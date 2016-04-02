package com.i10n.tools.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>XPathUtils</code> class is a simple utility class for dealing with
 * XPath introspection.
 * <p>
 */
public final class XPathUtils {
    private XPathUtils() {
    };

    /**
     * Returns a {@link List} of the {@link Node}s matching the given xpath, in
     * the context of the given root. Will return an empty list, if no such
     * nodes exist.
     * 
     * @param root
     * @param xpath
     * 
     * @return
     */
    public static List<Node> getNodes(Node root, String xpath) {
        NodeList nodeList = getNodeList(root, xpath);
        List<Node> nodes = new ArrayList<Node>();
        if (null != nodeList) {
            int len = nodeList.getLength();
            for (int i = 0; i < len; i++) {
                nodes.add(nodeList.item(i));
            }
        }
        return nodes;
    }

    /**
     * Returns first {@link Node}s matching the given xpath, in the context of
     * the given root. Will return null, if no such nodes exist.
     * 
     * @param root
     * @param xpath
     * 
     * @return
     */
    public static Node getNode(Node root, String xpath) {
        Node node = null;
        try {
            XPathExpression expression = XPATH.compile(xpath);
            node = (Node) expression.evaluate(root, XPathConstants.NODE);

        }
        catch (XPathExpressionException e) {
            /* TODO : Handle Exception */

        }
        catch (ClassCastException e) {
            /* TODO : Handle Exception */
        }
        return node;
    }

    /**
     * Returns a {@link NodeList} matching the given xpath in the context of the
     * given root.
     * 
     * @param root
     * @param xpath
     * @return
     */
    public static NodeList getNodeList(Node root, String xpath) {
        NodeList nodeList = null;

        try {
            XPathExpression expression = XPATH.compile(xpath);
            nodeList = (NodeList) expression.evaluate(root, XPathConstants.NODESET);
        }
        catch (XPathExpressionException e) {
            /* TODO : Handle Exception */

        }
        catch (ClassCastException e) {
            /* TODO : Handle Exception */
        }
        return nodeList;
    }

    /**
     * Returns the attribute value with the given id for the Node
     * 
     * @param n
     * @param id
     * @return
     */
    public static String getAttribute(Node n, String id) {
        String result = null;
        NamedNodeMap attrMap = n.getAttributes();
        if (null != attrMap) {
            Node attr = attrMap.getNamedItem(id);
            result = (attr == null) ? null : attr.getNodeValue();
        }
        if (null != result) {
            result = result.trim();
        }
        return result;
    }

    private static final XPathFactory FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = FACTORY.newXPath();

}
