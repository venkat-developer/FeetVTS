package com.i10n.tools.utils.graphs;

/**
 * Interface to be implemented by all the elements that belong to a rooted
 * graph. Each element is a node in the Graph.
 * 
 * Deep checking is valid for predecessors because in a rooted graph, the check
 * end with the root node. The same need not be true for a successor
 * 
 * @author: N.Balaji
 */

public interface IRootedGraphElement {

    /**
     * adds a IRootedGraphElemnt instance as a predecessor to this node
     * 
     * @return true - if the supplied node is already not the predecessor of
     *         this node false - otherwise
     * 
     * @throws IllegalArugmentException
     *             - if the supplied argument is not an instance of type
     *             NodeDependencyGraphElement
     * 
     *             NullPointerException - when trying to add a predecessor to
     *             the root element
     */
    boolean addImmediatePredecessor(IRootedGraphElement predecessor);

    /**
     * adds a IRootedGraphElemnt instance as a successor to this node
     * 
     * @return true if the supplied node is already not the successor of this
     *         node false - otherwise
     * 
     * @throws IllegalArugmentException
     *             if the supplied argument is not an instance of type
     *             NodeDependencyGraphElement
     */
    boolean addImmediateSuccessor(IRootedGraphElement successor);

    /**
     * 
     * @param node
     *            The node that has has to be tested for the predecessor
     *            relation
     * @param immediate
     *            check for only immediate predecessors? or scan deep?
     * 
     * @return - true if the node is a predecessor
     */
    boolean isPredecessor(IRootedGraphElement node, boolean immediate);

    /**
     * @param node
     *            - The node that has has to be tested for the successor
     *            relation
     */
    boolean isSuccessor(IRootedGraphElement node);
}
