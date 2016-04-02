package com.i10n.tools.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.tools.skins.config.SkinProperties;

/**
 * Helper class for Parsing an XML File
 * 
 * @author N.Balaji
 */

public final class XMLUtils {

    private static final Logger LOG = Logger.getLogger(XMLUtils.class);

    /**
     * @Param: filename - Cannot be relative to the class - The file should not
     *         contain package paths - Should be present where the root package
     *         is present
     */
    public static Document stringToDocument(String docAsString) {
        InputStream stream = null;
        Document doc = null;
        try {
            stream = new ByteArrayInputStream(docAsString.getBytes());
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();

            doc = docBuilder.parse(stream);

        }
        catch (SAXException e) {
            e.printStackTrace();
            LOG.error("Caught SAXException while parsing xmlString", e);
        }
        catch (IOException e) {
            e.printStackTrace();
            LOG.error("Caught IOException while parsing xmlString", e);
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            LOG.error("Caught ParserConfigurationException while parsing xmlString", e);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
        return doc;

    }

    /**
     * Converts DOM Document given to String
     * 
     * @param inputDoc
     * @return
     */
    public static String documentToString(Node inputNode) {
        StringWriter writer = new StringWriter();
        Source domSource = new DOMSource(inputNode);
        String result = "";
        try {
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(domSource, new StreamResult(writer));
            result = writer.toString();
        }
        catch (TransformerException e) {
            e.printStackTrace();
            LOG
                    .error(
                            "Caught TransformerException while converting xmlNode to String",
                            e);
        }
        finally {
            IOUtils.closeQuietly(writer);
        }
        return result;
    }

    public static void transform(Source XSLTFile, Source XMLFile, Result result)
            throws TransformerException {

        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(new URIResolver() {

            public Source resolve(String href, String base) throws TransformerException {
                String staticDir = SkinProperties
                        .getProperty(SkinProperties.XSLT_DIR_KEY);
                href = staticDir + "/" + href;
                InputStream dependencyStream = this.getClass().getResourceAsStream(href);
                return new StreamSource(dependencyStream);
            }

        });
        Templates transformation = factory.newTemplates(XSLTFile);
        Transformer transformer = transformation.newTransformer();
        transformer.transform(XMLFile, result);

    }

    public static String transformToString(Source XSLTFile, Source XMLFile)
            throws TransformerException {
        StringWriter writer = new StringWriter();
        transform(XSLTFile, XMLFile, new StreamResult(writer));
        return writer.toString();
    }

    public static Document transformToDocument(Source XSLTFile, Source XMLFile)
            throws TransformerException {
        String xmlString = transformToString(XSLTFile, XMLFile);
        return stringToDocument(xmlString);
    }

    public static void removeElements(List<Node> listOfElements) {
        /* Method for removing all the elements in a list */
        for (Node node : listOfElements) {
            node.getParentNode().removeChild(node);
        }
    }

}
