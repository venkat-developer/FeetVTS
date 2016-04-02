package com.i10n.tools.utils.graphs;

/**
 * Interface to be implemented by all rooted graphs
 * 
 * 
 * @author: N.Balaji
 */

public interface IRootedGraph<T> {

    /**
     * @return: the root element of the rooted graph
     */
    public IRootedGraphElement getRootElement();

    /**
     * The Method through which an element of the graph is created. Does not
     * take responsibility for wiring the predecessors and successors
     */
    public IRootedGraphElement createElement(T sourceElement);
}
