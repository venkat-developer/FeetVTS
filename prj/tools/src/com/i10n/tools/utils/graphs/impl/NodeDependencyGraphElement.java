package com.i10n.tools.utils.graphs.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

import com.i10n.tools.utils.graphs.IRootedGraphElement;

/**
 * A rooted Graph of nodes that helps in finding dependency among nodes
 * 
 * @see IRootedGraphElement
 * 
 * @author N. Balaji
 * 
 */
public class NodeDependencyGraphElement implements IRootedGraphElement {

    private Set<NodeDependencyGraphElement> m_predecessors;
    private Set<NodeDependencyGraphElement> m_successors;
    private String m_key;

    /**
     * A new element can only be produced from a rooted graph
     */
    public NodeDependencyGraphElement(Node sourceNode, boolean root) {

        /* Setting the key */
        m_key = sourceNode.getAttributes().getNamedItem("name").getTextContent()
                .toString().trim();

        if (!root) {
            m_predecessors = new HashSet<NodeDependencyGraphElement>();
            m_successors = new HashSet<NodeDependencyGraphElement>();
        }
        else {
            m_predecessors = null;
            m_successors = new HashSet<NodeDependencyGraphElement>();
        }

    }

    /**
     * 
     * (non-Javadoc)
     * 
     * @see IRootedGraphElement#addImmediatePredecessor(IRootedGraphElement)
     */
    public boolean addImmediatePredecessor(IRootedGraphElement predecessor) {

        if (!isPredecessor(predecessor, true)) {
            m_predecessors.add((NodeDependencyGraphElement) predecessor);
            return true;
        }
        return false;
    }

    /**
     * 
     * (non-Javadoc)
     * 
     * @see IRootedGraphElement#addImmediateSuccessor(IRootedGraphElement)
     */
    public boolean addImmediateSuccessor(IRootedGraphElement successor) {

        if (!isSuccessor(successor)) {
            m_successors.add((NodeDependencyGraphElement) successor);
            return true;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see IRootedGraphElement#isPredecessor(IRootedGraphElement, boolean)
     */
    public boolean isPredecessor(IRootedGraphElement node, boolean immediate) {
        if (immediate) {
            if (m_predecessors.contains(node))
                return true;
            else
                return false;
        }
        else {
            return isDeepPredecessor((NodeDependencyGraphElement) node);
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see IRootedGraphElement#isSuccessor(IRootedGraphElement)
     */
    public boolean isSuccessor(IRootedGraphElement node) {

        /* Checking the validity of the arguments */
        if (!(node instanceof NodeDependencyGraphElement))
            throw new IllegalArgumentException(
                    "the argument passed must be an instance of class: NodeDependencyGraphElement");
        if (m_successors.contains(node))
            return true;
        else
            return false;
    }

    public String getKey() {
        return m_key;
    }

    public void setKey(String key) {
        m_key = key;
    }

    /**
     * (non-Javadoc)
     * 
     * Overriding the equals method to compare objects by key
     */
    @Override
    public boolean equals(Object obj) {
        if (this.m_key.equalsIgnoreCase(((NodeDependencyGraphElement) obj).m_key))
            return true;
        else
            return false;
    }

    /**
     * (non-Javadoc)
     * 
     * Overriding the hash code method of the object class to maintain the
     * relation between the equals and hashCode method as mentioned in the
     * documentation for the object
     * 
     * @return: the hash code of the key string
     */
    @Override
    public int hashCode() {
        return m_key.hashCode();
    }

    private boolean isDeepPredecessor(NodeDependencyGraphElement node) {
        /**
         * TODO : Can be optimized
         */
        /* Declaring the temporary place holder */
        List<NodeDependencyGraphElement> bfsQueue = new ArrayList<NodeDependencyGraphElement>();
        bfsQueue.add(this);
        boolean isPredecessor = false;

        /* Doing a BFS from this node through the predecessors upto the root */
        for (int i = 0; i < bfsQueue.size(); i++) {
            if (bfsQueue.contains(node)) {
                isPredecessor = true;
                break;
            }
            else {
                if (bfsQueue.get(i).m_predecessors != null)
                    bfsQueue.addAll(bfsQueue.get(i).m_predecessors);
            }
        }

        return isPredecessor;

    }
}
