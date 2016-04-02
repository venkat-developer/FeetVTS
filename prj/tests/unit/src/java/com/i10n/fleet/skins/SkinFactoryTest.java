package com.i10n.fleet.skins;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.i10n.fleet.test.AbstractFleetTestCase;

/**
 * Test Cases for testing {@link SkinFactory}
 * 
 * @author subramaniam
 * 
 */
public class SkinFactoryTest extends AbstractFleetTestCase {

    /**
     * To Test SkinFactory Functionality
     */
    @Test
    public void testSkinFactory() {

        SkinFactory skinfactory = new SkinFactory();
        DocumentBuilder docBuilder = null;
        InputStream stream = SkinFactoryTest.class
                .getResourceAsStream("/skins/skins.xml");
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            assertNotNull(skinfactory.getSkins(doc).get("default"));
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail("Caught ParserConfigurationException while parsing skins.xml : "
                    + e.getMessage());
        }
        catch (SAXException e) {
            e.printStackTrace();
            fail("Caught SAXException while parsing skins.xml : " + e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Caught IOException while parsing skins.xml : " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Tests Exception Cases
     */
    @Test
    public void testExceptionHandling() {
        /**
         * Tests for a badly formed skin xml
         */
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><skins><skin name=\"default\" dir=\"bad-default/\" config=\"default.out.txt\"/></skins>";
        doTestException(xmlString);
        /**
         * Tests for a non existing xml file
         */
        xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><skins><skin name=\"bad-default\" dir=\"baddefault/\" config=\"bad.default.out.txt\"/></skins>";
        doTestException(xmlString);

    }

    private void doTestException(String xmlString) {
        SkinFactory skinfactory = new SkinFactory();
        DocumentBuilder docBuilder = null;
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(xmlString.getBytes());
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            assertTrue(skinfactory.getSkins(doc).size() == 0);
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail("Caught ParserConfigurationException while parsing skins.xml : "
                    + e.getMessage());
        }
        catch (SAXException e) {
            e.printStackTrace();
            fail("Caught SAXException while parsing skins.xml : " + e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Caught IOException while parsing skins.xml : " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
