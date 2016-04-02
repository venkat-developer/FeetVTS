package com.i10n.tools.skins;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.utils.XPathUtils;

/**
 * Builds the hierarchy of skins
 * 
 * @author sabarish
 * 
 */
public class SkinHierarchyBuilder {

    private SkinDependencyManager m_dependencyManager = null;
    private Map<String, Skin> m_skins = new LinkedHashMap<String, Skin>();

    private static final Logger LOG = Logger.getLogger(SkinHierarchyBuilder.class);

    public SkinHierarchyBuilder(SkinDependencyManager dependencyManager) {
        m_dependencyManager = dependencyManager;
    }

    /**
     * Starts populating skins map. Parses skins.xml in Skin Directory and uses
     * the information in the xml to parse further files
     */
    private void startPopulating() {

        DocumentBuilder docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        try {
            in = new FileInputStream(SkinProperties
                    .getProperty(SkinProperties.SKIN_DIR_KEY)
                    + SkinConstants.DELIMITER + "skins.xml");
            Document skinsDoc = docBuilder.parse(in);
            List<Node> skinNodes = XPathUtils.getNodes(skinsDoc, "/skins/skin");
            for (Node skinNode : skinNodes) {
                /*
                 * Parsing individual Skins in skins.xml
                 */
                String skinName = XPathUtils.getAttribute(skinNode, "name");
                String configFile = XPathUtils.getAttribute(skinNode, "config");
                String skinDir = XPathUtils.getAttribute(skinNode, "dir");
                String xmlFile = SkinProperties.getProperty(SkinProperties.SKIN_DIR_KEY)
                        + SkinConstants.DELIMITER + skinDir + configFile;
                InputStream stream = null;
                try {
                    stream = new FileInputStream(xmlFile);
                    Document skinDoc = docBuilder.parse(stream);
                    Skin skin = new Skin(skinName, skinDoc);
                    m_skins.put(skinName, skin);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LOG.error("Caught FileNotFoundException while parsing skin - "
                            + skinName, e);
                }
                catch (SAXException e) {
                    e.printStackTrace();
                    LOG.error("Caught SAXException while parsing skin - " + skinName, e);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    LOG.error("Caught IOException while parsing skin - " + skinName, e);
                }
                finally {
                    IOUtils.closeQuietly(stream);
                }

            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            LOG.error("Caught FileNotFoundException while parsing skins.xml", e);
        }
        catch (SAXException e) {
            e.printStackTrace();
            LOG.error("Caught SAXException while parsing skins.xml", e);
        }
        catch (IOException e) {
            e.printStackTrace();
            LOG.error("Caught IOException while parsing skins.xml", e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void buildHierarchy() throws SkinParseException {
        startPopulating();
        dependencyCheck();
    }

    private void dependencyCheck() throws SkinParseException {

        m_skins = m_dependencyManager.manageSkinDependency(m_skins);
    }

    /**
     * Returns the list of skins
     * 
     * @return
     */
    public List<Skin> getSkins() {
        return new ArrayList<Skin>(m_skins.values());
    }

    /**
     * Returns the {@link Skin} with the specified name
     * 
     * @param name
     * @return
     */
    public Skin getSkin(String skin) {
        return m_skins.get(skin);
    }

    /**
     * Returns the hierarchy of the given {@link Skin}
     * 
     * @param skin
     * @return
     */
    public List<Skin> getHierarchy(Skin skin) {
        List<Skin> result = new ArrayList<Skin>();
        Skin currentSkin = skin;
        while (currentSkin != null) {
            result.add(currentSkin);
            Skin baseSkin = currentSkin.getBaseSkin();
            currentSkin = baseSkin;
        }
        return result;
    }

}
