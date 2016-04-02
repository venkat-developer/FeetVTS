package com.i10n.fleet.util;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.i10n.fleet.test.AbstractFleetTestCase;

/**
 * Tests {@link XPathUtils} for variuos conditions
 * 
 * @author sabarish
 * 
 */
public class XPathUtilsTest extends AbstractFleetTestCase {

    private Document m_document = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/xpathutils/xpath.xml");
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            m_document = docBuilder.parse(stream);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Tests {@link XPathUtils#getNode(Node, String)}
     */
    @Test
    public void testNodeRetrieval() {
        List<Node> nodes = XPathUtils.getNodes(m_document, "/tag-first/tag-second");
        NodeList nodelist = XPathUtils.getNodeList(m_document, "/tag-first/tag-second");
        assertEquals(nodes.size(), 4);
        assertEquals(nodelist.getLength(), 4);
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            Node node = nodelist.item(i);
            assertTrue(nodes.contains(nodelist.item(i)));
            assertNotNull(XPathUtils.getNode(node, "tag-third"));
        }
    }

    /**
     * Tests {@link XPathUtils#getAttribute(Node, String)}
     */
    @Test
    public void testAttributeRetrieval() {
        List<Node> nodes = XPathUtils.getNodes(m_document,
                "/tag-first/tag-second/tag-third");
        for (Node node : nodes) {
            assertEquals(XPathUtils.getAttribute(node, "id"), "third");
        }
    }

    /**
     * Test {@link XPathUtils} for Exception cases
     */
    public void testExceptionCase() {
        Node node = XPathUtils.getNode(m_document, "/x/y");
        assertNull(node);
        node = XPathUtils.getNode(m_document, "$$$$");
        assertNull(node);
        assertNull(XPathUtils.getAttribute(XPathUtils.getNode(m_document,
                "/tag-first/tag-second/tag-third"), "idx"));
        NodeList nodelist = XPathUtils.getNodeList(m_document, "/x/y");
        assertEquals(nodelist.getLength(), 0);
        nodelist = XPathUtils.getNodeList(m_document, "$$$$");
        assertNull(nodelist);

    }

}
