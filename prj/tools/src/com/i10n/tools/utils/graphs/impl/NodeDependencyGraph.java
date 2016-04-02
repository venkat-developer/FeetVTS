package com.i10n.tools.utils.graphs.impl;

import org.w3c.dom.Node;

import com.i10n.tools.utils.graphs.IRootedGraph;

/**
 * A Node Rooted Graph representing a Node Dependency
 * 
 * @see IRootedGraph
 * 
 * @author N. Balaji
 * 
 */
public class NodeDependencyGraph implements IRootedGraph<Node> {
    private NodeDependencyGraphElement m_rootElement;

    public NodeDependencyGraph(Node rootNode) {
        m_rootElement = new NodeDependencyGraphElement(rootNode, true);
    }

    /**
     * (non-Javadoc)
     * 
     * @see IRootedGraph#getRootElement()
     */
    public NodeDependencyGraphElement getRootElement() {
        return m_rootElement;
    }

    /**
     * (non-Javadoc)
     * 
     * @see IRootedGraph#createElement(Object)
     * 
     * 
     * @return: A new node that can be wired to other nodes in the rooted graph
     * 
     * @throws: IllegalArgumentException - when the argument supplied is not an
     *          instance of org.w3c.dom.Node
     */
    public NodeDependencyGraphElement createElement(Node sourceElement) {
        return new NodeDependencyGraphElement(sourceElement, false);
    }

}
