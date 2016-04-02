package com.i10n.fleet.skins;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.XPathUtils;

import freemarker.ext.dom.NodeModel;

/**
 * A factory for creating {@link Dataset} based on the skin xml file specified
 * 
 * @see ISkinFactory
 * @author sabarish
 * 
 */
public class SkinFactory implements ISkinFactory {

    private static final Logger LOG = Logger.getLogger(SkinFactory.class);

    /**
     * Returns a {@link Dataset} from the skin config file(contains the list of
     * skins) specified.The Dataset contains a set of (SkinName,SkinDocument)
     * pairs
     * 
     * @param doc
     * @return A dataset which contains the (SkinName,SkinDocment) pair for all
     *         skins that are specified in the skins.xml
     */
    public Dataset getSkins(Document doc) {
        return getSupportedSkins(doc);
    }

    /**
     * Populates the {@link Dataset} with the (SkinName,SkinDocument) pair
     * 
     * @param dataset
     * @return whether population was successful
     * @throws Exception
     */
    private Dataset populateDataset(String skinDir, String skinName, String skinXML)
            throws ParserConfigurationException, IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream(
                "/skins" + "/" + skinDir + "/" + skinXML);
        Dataset result = new Dataset();
        if (null != stream) {
            try {
                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = docBuilder.parse(stream);
                try {
                    NodeModel.useJaxenXPathSupport();
                }
                catch (Exception e) {
                    LOG
                            .error(
                                    "No jaxen xpath support is found so continuing with default xpath support",
                                    e);
                }
                NodeModel.simplify(doc);
                result.put(skinName, doc);
            }
            finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return result;
    }

    /**
     * Returns the list of supported skins from the skin config file parsed.
     * 
     * @param doc
     * @return
     */
    private Dataset getSupportedSkins(Document doc) {
        Dataset result = new Dataset();
        List<Node> nodes = XPathUtils.getNodes(doc, "/skins/skin");
        for (Node node : nodes) {
            String skinDir = XPathUtils.getAttribute(node, "dir");
            String skinXML = XPathUtils.getAttribute(node, "config");
            String skinName = XPathUtils.getAttribute(node, "name");
            try {
                result.putAll(populateDataset(skinDir, skinName, skinXML));
            }
            catch (ParserConfigurationException e) {
                LOG
                        .error(
                                "Caught Parser Configuration Exception while building Document Builder Factory",
                                e);
            }
            catch (SAXException e) {
                LOG.error("Caught SAXException while parsing SkinConfig file", e);
            }
            catch (IOException e) {
                LOG.error("Caught IOException while parsing SkinConfig file:", e);
            }
        }
        return result;
    }
}
