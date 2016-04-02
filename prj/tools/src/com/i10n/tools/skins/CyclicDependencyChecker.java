package com.i10n.tools.skins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.utils.XPathUtils;
import com.i10n.tools.utils.graphs.impl.NodeDependencyGraph;
import com.i10n.tools.utils.graphs.impl.NodeDependencyGraphElement;

/**
 * Class that checks for the existence of cyclic dependencies in the supplied
 * list of nodes
 * 
 * @author: N.Balaji
 */
public class CyclicDependencyChecker {

    public CyclicDependencyChecker() {
        /* Configuring this object */
    }

    private boolean isSafe(Node startingNode, String pathToReferenceNode,
            Map<String, Node> completeListOfNodes) throws SkinParseException {
        /* Declaring necessary classes */
        NodeDependencyGraph widgetDependencyGraph = new NodeDependencyGraph(startingNode);
        NodeDependencyGraphElement rootElement = widgetDependencyGraph.getRootElement();
        List<NodeDependencyGraphElement> graphElementQueue = new ArrayList<NodeDependencyGraphElement>();
        boolean safe = true;

        /* Putting the starting element into the queue */
        graphElementQueue.add(rootElement);

        /*
         * Trying to add all the deep widget references. If an node is referring
         * to any of its predecessors then a cycle exists.
         */
        for (int l = 0; l < graphElementQueue.size(); l++) {
            NodeDependencyGraphElement curElement = graphElementQueue.get(l);
            Node equivalentNodeRepresentation = completeListOfNodes.get(curElement
                    .getKey());
            List<Node> childNodeList = extractReferences(equivalentNodeRepresentation,
                    pathToReferenceNode);
            for (int i = 0; i < childNodeList.size(); i++) {
                Node childNode = childNodeList.get(i);
                Node referencedNode = completeListOfNodes.get(XPathUtils.getAttribute(
                        childNode, "ref"));
                if (null == referencedNode) {
                    String nodeName = XPathUtils.getAttribute(childNode, "name");
                    throw new SkinParseException(
                            "The referenced element"
                                    + nodeName
                                    + " in the supplied XML file does not contain a ref attribute or has a wrong reference");
                }
                /* Creating a Graph element for the reference node */
                NodeDependencyGraphElement referencedElement = (NodeDependencyGraphElement) widgetDependencyGraph
                        .createElement(referencedNode);
                /* Trying to wire things up */
                if (!curElement.isPredecessor(referencedElement, false)) {
                    referencedElement.addImmediatePredecessor(curElement);
                    curElement.addImmediateSuccessor(referencedElement);
                    graphElementQueue.add(referencedElement);
                }
                else {
                    safe = false;
                    break;
                }
            }
            if (!safe) {
                break;
            }
        }
        return safe;
    }

    /**
     * Checks for cycle on the reference nodes with the list of wideget nodes
     * 
     * @param listOfNodes
     * @param pathToReferenceNode
     * @return
     * @throws SkinParseException
     */
    public boolean checkForCycle(List<Node> listOfNodes, String pathToReferenceNode)
            throws SkinParseException {

        Map<String, Node> completeMapOfNodes = (HashMap<String, Node>) createMapOfNodes(listOfNodes);
        boolean isSafe = true;
        for (Node curNode : listOfNodes)
            isSafe &= isSafe(curNode, pathToReferenceNode, completeMapOfNodes);
        return (!isSafe);
    }

    private Map<String, Node> createMapOfNodes(List<Node> listOfNodes) {
        Map<String, Node> completeMapOfNodes = new HashMap<String, Node>();
        for (Node curNode : listOfNodes) {
            completeMapOfNodes.put(XPathUtils.getAttribute(curNode, "name"), curNode);
        }
        return completeMapOfNodes;
    }

    private List<Node> extractReferences(Node curNode, String pathToReferenceNode) {
        List<Node> result;
        result = XPathUtils.getNodes(curNode, pathToReferenceNode);
        return result;
    }

}
